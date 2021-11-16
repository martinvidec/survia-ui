package at.videc.survia.view;

import at.videc.survia.config.AppProperties;
import at.videc.survia.controller.ErrorHandlingController;
import at.videc.survia.view.base.BaseView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.StatusControllerApi;
import org.openapitools.client.model.ApplicationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;


@Route(value = "indicators", layout = MainView.class)
public class IndicatorsView extends BaseView {

    private final static Logger LOG = LoggerFactory.getLogger(IndicatorsView.class);

    private AppProperties properties;
    private ErrorHandlingController errorHandlingController;

    @Autowired
    public IndicatorsView(AppProperties properties, ErrorHandlingController errorHandlingController) {
        this.properties = properties;
        this.errorHandlingController = errorHandlingController;

        add(new H3("Indicators View"));
    }
}
