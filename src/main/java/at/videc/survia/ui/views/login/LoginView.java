package at.videc.survia.ui.views.login;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Optional;

//@AnonymousAllowed
//@PageTitle("Login")
//@Route(value = "login")
//@RouteAlias(value = "")
public class  LoginView extends LoginOverlay implements BeforeEnterObserver {
    private static final String KEYCLOAK_URL = "/oauth2/authorization/keycloak";

    private final Optional<OidcUser> authenticatedUser;

    public LoginView(AuthenticationContext authenticationContext) {
        this.authenticatedUser = authenticationContext.getAuthenticatedUser(OidcUser.class);
        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Survia");
        i18n.getHeader().setDescription("Login using user/user or admin/admin");
        i18n.setAdditionalInformation(null);

        getFooter().add(createFooter());

        setI18n(i18n);

        setForgotPasswordButtonVisible(false);
        setOpened(true);
    }

    private static Button createFooter() {
        Button loginButton = new Button("Login with Keycloak");
        loginButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        loginButton.addClickListener(event -> event.getSource().getUI().ifPresent(ui -> ui.getPage().open(KEYCLOAK_URL, "_self")));
        return loginButton;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.isPresent()) {
            // Already logged in
            setOpened(false);
            event.forwardTo("/hello");
        }

        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}
