package at.videc.survia.controller.base;

import org.openapitools.client.ApiClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

public abstract class AuthenticatedApiController<T> {

    protected T getAuthenticatedApi() {
        T api = getApi();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        getApiClient(api).addDefaultHeader("Authorization", "Bearer " + ((DefaultOidcUser) token.getPrincipal()).getIdToken().getTokenValue());
        return api;
    }

    protected abstract T getApi();

    protected abstract ApiClient getApiClient(T api);
}
