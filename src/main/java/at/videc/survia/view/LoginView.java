package at.videc.survia.view;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Adds an explicit link that the user has to click to login.
 */
@Route("login")
@PageTitle("Login")
public class LoginView extends VerticalLayout {

    private static final String URL = "/oauth2/authorization/github";

    private static final String URL2 = "/oauth2/authorization/google";

    /**
     * This methods gets the user into google sign in page.
     */
    public LoginView() {
        Anchor githubLoginButton = new Anchor(URL, "Login with Github");
        Anchor googleLoginButton = new Anchor(URL2, "Login with Google");
        add(githubLoginButton);
        add(googleLoginButton);
        setSizeFull();
    }
}
