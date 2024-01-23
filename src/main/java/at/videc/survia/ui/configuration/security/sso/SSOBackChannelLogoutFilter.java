package at.videc.survia.ui.configuration.security.sso;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.client.oidc.authentication.logout.LogoutTokenClaimNames;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Objects;

public class SSOBackChannelLogoutFilter extends GenericFilterBean {

    /* Value defined by the specification */
    static final String LOGOUT_TOKEN = "logout_token";

    static final String REGISTRATION_ID = "registrationId";

    private static final String DID_NOT_MATCH_REQUEST = "Did not match request to %s";

    private final SessionRegistry sessionRegistry;

    private final ClientRegistrationRepository clientRegistrationRepository;

    private final JwtDecoderFactory<ClientRegistration> jwtDecoderFactory;

    private RequestMatcher requestMatcher = new AntPathRequestMatcher(SSOProperties.DEFAULT_BACKCHANNEL_LOGOUT_ROUTE);

    /**
     * Creates an instance of the filter.
     *
     * @param sessionRegistry            the session registry, {@code not null}
     * @param clientRegistrationRepository the client-registration repository, {@code not null}
     */
    public SSOBackChannelLogoutFilter(
            SessionRegistry sessionRegistry,
            ClientRegistrationRepository clientRegistrationRepository
    ) {
        this(sessionRegistry, clientRegistrationRepository, clientRegistration -> {
            final String issuerUri = clientRegistration.getProviderDetails().getIssuerUri();
            return JwtDecoders.fromOidcIssuerLocation(issuerUri);
        });
    }

    /**
     * Creates an instance of the filter.
     *
     * @param sessionRegistry            the session registry, {@code not null}
     * @param clientRegistrationRepository the client-registration repository, {@code not null}
     * @param jwtDecoderFactory           the factory for creating a {@link JwtDecoder} for
     *                                    decoding the logout token, {@code not null}
     */
    SSOBackChannelLogoutFilter(
            SessionRegistry sessionRegistry,
            ClientRegistrationRepository clientRegistrationRepository,
            JwtDecoderFactory<ClientRegistration> jwtDecoderFactory
    ) {
        Objects.requireNonNull(sessionRegistry);
        Objects.requireNonNull(clientRegistrationRepository);
        Objects.requireNonNull(jwtDecoderFactory);
        this.sessionRegistry = sessionRegistry;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.jwtDecoderFactory = jwtDecoderFactory;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (requiresLogout(httpRequest)) {
            logger.debug("Back-Channel request requires logout");
            performLogout(httpRequest, httpResponse);
        }
        chain.doFilter(request, response);
    }

    private void performLogout(HttpServletRequest request, HttpServletResponse response) throws JwtValidationException {
        final ClientRegistration clientRegistration = getClientRegistration(request, response);
        if (clientRegistration == null) {
            // Set the response status to Http 400 (see Spec)
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        final String token = getToken(request, response);
        if (token == null) {
            // Set the response status to Http 400 (see Spec)
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        final JwtDecoder decoder = jwtDecoderFactory.createDecoder(clientRegistration);
        final Jwt jwt = decoder.decode(token);
        // Validate the logout token
        if (isInvalidLogoutToken(response, clientRegistration, jwt)) {
            // Set the response status to Http 400 (see Spec)
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        final String tokenSubject = jwt.getSubject();
        final String tokenSid = jwt.getClaimAsString(LogoutTokenClaimNames.SID);
        expireUserSessions(tokenSid, tokenSubject);

        // Set the response status to Http 200 (see Spec)
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private String getToken(HttpServletRequest request, HttpServletResponse response) {
        final String token = request.getParameter(LOGOUT_TOKEN);

        if (token == null) {
            logger.warn("Back-Channel logout request missing parameter: " + LOGOUT_TOKEN);
            return null;
        }
        return token;
    }

    private ClientRegistration getClientRegistration(HttpServletRequest request, HttpServletResponse response) {
        final String clientRegistrationId = requestMatcher.matcher(request).getVariables().get(REGISTRATION_ID);

        if (clientRegistrationId == null) {
            logger.warn("Back-Channel logout. Required registrationId URI variable is missing:" + REGISTRATION_ID);
            return null;
        }

        final ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);

        if (clientRegistration == null) {
            logger.warn("Client registration not found: " + clientRegistrationId);
            return null;
        }
        return clientRegistration;
    }

    private boolean isInvalidLogoutToken(HttpServletResponse response, ClientRegistration clientRegistration, Jwt jwt) {
        final SSOLogoutTokenValidator tokenValidator = new SSOLogoutTokenValidator(clientRegistration);
        if (tokenValidator.validate(jwt).hasErrors()) {
            logger.warn("Invalid logout token");
            return true;
        }
        return false;
    }

    /**
     * Expires all user sessions that match the token subject or sid.
     *
     * @param tokenSid    the token sid
     * @param tokenSubject the token subject
     */
    private void expireUserSessions(String tokenSid, String tokenSubject) {
        sessionRegistry.getAllPrincipals().stream().filter(principal -> {
            if (principal instanceof OidcUser user) {
                // Check if the token subject or sid matches the user's subject
                if (tokenSid != null) {
                    final String userSid = user.getClaimAsString(LogoutTokenClaimNames.SID);
                    return Objects.equals(tokenSid, userSid);
                } else {
                    final String userSub = user.getSubject();
                    return Objects.equals(tokenSubject, userSub);
                }
            } else {
                return false;
            }
        }).flatMap(p -> sessionRegistry.getAllSessions(p, false).stream()).forEach(SessionInformation::expireNow);
    }

    private boolean requiresLogout(HttpServletRequest request) {
        if (requestMatcher.matches(request)) {
            return true;
        }
        if (logger.isTraceEnabled()) {
            logger.trace(LogMessage.format(DID_NOT_MATCH_REQUEST, requestMatcher));
        }
        return false;
    }

    /**
     * Returns the request-matcher for this filter.
     *
     * @return the request-matcher, not {@code null}
     */
    public RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }

    /**
     * Sets the request-matcher for this filter.
     *
     * @param logoutRequestMatcher the request-matcher, not {@code null}
     */
    public void setRequestMatcher(RequestMatcher logoutRequestMatcher) {
        requestMatcher = Objects.requireNonNull(logoutRequestMatcher);
    }

    /**
     * Sets the request-matcher for this filter.
     *
     * @param backChannelLogoutRoute the request-matcher, not {@code null}
     */
    public void setBackChannelLogoutRoute(String backChannelLogoutRoute) {
        Objects.requireNonNull(backChannelLogoutRoute);
        setRequestMatcher(new AntPathRequestMatcher(backChannelLogoutRoute, "POST"));
    }

}
