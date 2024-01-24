package at.videc.survia.ui.configuration.security.sso;

import at.videc.survia.ui.configuration.security.KeycloakRoleMapper;
import com.vaadin.flow.spring.security.VaadinSavedRequestAwareAuthenticationSuccessHandler;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.boot.autoconfigure.security.oauth2.client.ClientsConfiguredCondition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Objects;

/**
 * The SSO configuration.
 * <br/>
 * see <a href="https://openid.net/specs/openid-connect-core-1_0.html">https://openid.net/specs/openid-connect-core-1_0.html</a>
 *
 */
@EnableWebSecurity
@Conditional(ClientsConfiguredCondition.class)
@EnableConfigurationProperties(SSOProperties.class)
@Configuration
public class SSOConfiguration extends VaadinWebSecurity {
    /**
     * The configuration properties.
     */
    private final SSOProperties properties;

    /**
     * The OIDC logout success handler.
     */
    private final OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler;

    /**
     * The Vaadin login success handler.
     */
    private final VaadinSavedRequestAwareAuthenticationSuccessHandler loginSuccessHandler;

    /**
     * The session registry.
     */
    private final SessionRegistry sessionRegistry;

    /**
     * The Back-Channel logout filter.
     */
    private final SSOBackChannelLogoutFilter backChannelLogoutFilter;

    /**
     * The login route.
     */
    private final String loginRoute;

    /**
     * The logout redirect route.
     */
    private final String logoutRedirectRoute;

    /**
     * The maximum number of concurrent sessions allowed per user.
     */
    private final int maximumSessions;

    /**
     * Creates an instance of this configuration bean.
     *
     * @param properties                   the configuration properties
     * @param sessionRegistry              the session registry
     * @param clientRegistrationRepository the client-registration repository
     */
    public SSOConfiguration(
            SSOProperties properties,
            SessionRegistry sessionRegistry,
            ClientRegistrationRepository clientRegistrationRepository
    ) {
        this.properties = properties;
        this.sessionRegistry = sessionRegistry;
        this.logoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        this.backChannelLogoutFilter = new SSOBackChannelLogoutFilter(sessionRegistry, clientRegistrationRepository);
        this.loginSuccessHandler = new VaadinSavedRequestAwareAuthenticationSuccessHandler();
        this.logoutSuccessHandler.setRedirectStrategy(new SSORedirectStrategy());
        this.loginRoute = Objects.requireNonNullElse(properties.getLoginRoute(), SSOProperties.DEFAULT_LOGIN_ROUTE);
        this.logoutRedirectRoute = Objects.requireNonNullElse(properties.getLogoutRedirectRoute(), SSOProperties.DEFAULT_LOGOUT_REDIRECT_ROUTE);
        this.maximumSessions = properties.getMaximumConcurrentSessions();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        configureOAuth2Login(http);
        configureLogout(http);
        configureExceptionHandling(http);
        configureSessionManagement(http);

        // Vaadin has its own CSRF protection
        http.csrf(AbstractHttpConfigurer::disable);
    }

    private void configureSessionManagement(HttpSecurity http) throws Exception {
        http.sessionManagement(sessionManagement -> {
            sessionManagement.sessionConcurrency(concurrency -> {
                // Sets the maximum number of concurrent sessions allowed per user
                concurrency.maximumSessions(maximumSessions);

                // Sets the session registry to keep track of the number of active sessions
                concurrency.sessionRegistry(sessionRegistry);

                // Sets the Vaadin-Refresh token to handle expired UIDL (= Vaadin Internal Request) requests
                final SessionInformationExpiredStrategy expiredStrategy = new SSOExpiredSessionStrategy();
                concurrency.expiredSessionStrategy(expiredStrategy);
            });
        });
    }

    private void configureExceptionHandling(HttpSecurity http) throws Exception {
        http.exceptionHandling(exceptionHandling -> {
            // Sets the login route as the entry point for the authentication exception handler
            LoginUrlAuthenticationEntryPoint entryPoint = new LoginUrlAuthenticationEntryPoint(loginRoute);
            exceptionHandling.authenticationEntryPoint(entryPoint);
        });
    }

    private void configureLogout(HttpSecurity http) throws Exception {
        http.logout(logout -> {
            // Sets the logout route as the endpoint for the logout filter
            logoutSuccessHandler.setPostLogoutRedirectUri(logoutRedirectRoute);
            logout.logoutSuccessHandler(logoutSuccessHandler);
        });

        if (properties.isBackChannelLogout()) {
            final String backChannelLogoutRoute = Objects.requireNonNullElse(properties.getBackChannelLogoutRoute(), SSOProperties.DEFAULT_BACKCHANNEL_LOGOUT_ROUTE);
            backChannelLogoutFilter.setBackChannelLogoutRoute(backChannelLogoutRoute);

            // Add the Back-Channel logout filter after the Spring Security Logout filter
            http.addFilterAfter(backChannelLogoutFilter, LogoutFilter.class);

            // Disable CSRF protection for the Back-Channel logout route
            final RequestMatcher matcher = backChannelLogoutFilter.getRequestMatcher();
            http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.ignoringRequestMatchers(matcher));
        }
    }

    private void configureOAuth2Login(HttpSecurity http) throws Exception {
        final String loginRoute = Objects.requireNonNullElse(properties.getLoginRoute(), SSOProperties.DEFAULT_LOGIN_ROUTE);

        http.oauth2Login(oauth2Login -> {
            // Sets the login success handler to redirect the web browser to the saved request
            RequestCache requestCache = http.getSharedObject(RequestCache.class);
            if (requestCache != null) {
                loginSuccessHandler.setRequestCache(requestCache);
            }
            oauth2Login.successHandler(loginSuccessHandler);

            // Permit all requests to the login route
            oauth2Login.loginPage(loginRoute).permitAll();

            // Sets the login route as endpoint for redirection when trying to access a protected view without authorization
            oauth2Login.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userAuthoritiesMapper(new KeycloakRoleMapper()));

            try {
                setLoginView(http, loginRoute);
            } catch (Exception e) {
                throw new RuntimeException("Can not set LoginView.", e);
            }
        });
    }
}
