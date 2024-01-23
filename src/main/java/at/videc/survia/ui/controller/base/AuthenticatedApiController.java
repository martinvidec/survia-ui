package at.videc.survia.ui.controller.base;

import at.videc.survia.restclient.ApiClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

public abstract class AuthenticatedApiController<T> {

    protected abstract T getApi();
}
