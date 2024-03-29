package at.videc.survia.ui.configuration.security.sso;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

/**
 * This class is used to refresh the OAuth2 access token if it is about to expire.
 */
@Component
public class SSOAccessTokenRefresher {

    public static final String REFRESH_TOKEN_GRANT = "refresh_token";

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    private final OAuth2AuthorizedClientService clientService;

    private final SSOProperties ssoProperties;

    /**
     * Creates a new instance of this class.
     *
     * @param authorizedClientManager the OAuth2 authorized client manager
     * @param clientService           the OAuth2 authorized client service
     */
    public SSOAccessTokenRefresher(
            OAuth2AuthorizedClientManager authorizedClientManager,
            OAuth2AuthorizedClientService clientService,
            SSOProperties ssoProperties
    ) {
        this.authorizedClientManager = authorizedClientManager;
        this.clientService = clientService;
        this.ssoProperties = ssoProperties;
    }

    /**
     * Refreshes the OAuth2 access token if it is about to expire.
     *
     * @return the current or the refreshed OAuth2 access token
     */
    public Optional<OAuth2AccessToken> refreshAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }

        if (authentication instanceof OAuth2AuthenticationToken auth2AuthenticationToken) {
            OAuth2AuthorizedClient authorizedClient = clientService.loadAuthorizedClient(auth2AuthenticationToken.getAuthorizedClientRegistrationId(), auth2AuthenticationToken.getName());

            if (authorizedClient == null ||
                    authorizedClient.getRefreshToken() == null ||
                    authorizedClient.getAccessToken() == null ||
                    authorizedClient.getAccessToken().getExpiresAt() == null
            ) {
                return Optional.empty();
            }

            // TODO make this configurable: seconds before expiration to refresh
            if (authorizedClient.getAccessToken().getExpiresAt().isAfter(Instant.now().plusSeconds(ssoProperties.getAccessTokenExpiredSkew()))) {
                return Optional.of(authorizedClient.getAccessToken());
            }

            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withAuthorizedClient(authorizedClient)
                    .principal(auth2AuthenticationToken)
                    .attributes(map -> map.put(OAuth2ParameterNames.GRANT_TYPE, REFRESH_TOKEN_GRANT))
                    .build();

            OAuth2AuthorizedClient reAuthorizedClient = this.authorizedClientManager.authorize(authorizeRequest);
            if (reAuthorizedClient == null || reAuthorizedClient.getAccessToken() == null) {
                return Optional.empty();
            }

            return Optional.of(reAuthorizedClient.getAccessToken());
        } else {
            return Optional.empty();
        }
    }
}