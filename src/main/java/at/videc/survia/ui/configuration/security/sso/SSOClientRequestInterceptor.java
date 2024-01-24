package at.videc.survia.ui.configuration.security.sso;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class SSOClientRequestInterceptor implements ClientHttpRequestInterceptor {

    private final SSOAccessTokenRefresher ssoAccessTokenRefresher;

    public SSOClientRequestInterceptor(SSOAccessTokenRefresher ssoAccessTokenRefresher) {
        this.ssoAccessTokenRefresher = ssoAccessTokenRefresher;
    }
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        Optional<OAuth2AccessToken> accessToken = ssoAccessTokenRefresher.refreshAccessToken();
        if (accessToken.isPresent()) {
            request.getHeaders().setBearerAuth(accessToken.get().getTokenValue());
            return execution.execute(request, body);
        }

        return execution.execute(request, body);
    }
}
