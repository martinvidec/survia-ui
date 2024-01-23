package at.videc.survia.ui.views.hello;

import at.videc.survia.ui.configuration.properties.AppProperties;
import at.videc.survia.ui.controller.ErrorHandlingController;
import at.videc.survia.ui.controller.StatusController;
import at.videc.survia.restclient.model.ApplicationResponse;
import at.videc.survia.ui.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.scheduling.annotation.Scheduled;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@PageTitle("Hello")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "",  layout = MainLayout.class)
@PermitAll
public class HelloView extends VerticalLayout {

    private final StatusController statusController;

    private final ErrorHandlingController errorHandlingController;

    private Binder<HelloView.ContextObject> binder;

    private HelloView.ContextObject contextObject;

    public HelloView(
            StatusController statusController,
            ErrorHandlingController errorHandlingController,
            AppProperties properties
    ) {
        this.statusController = statusController;
        this.errorHandlingController = errorHandlingController;
        contextObject = new HelloView.ContextObject();
        binder = new Binder<>(HelloView.ContextObject.class);

        setSpacing(false);

        TextField statusField = new TextField();
        statusField.setWidth("100%");
        statusField.setReadOnly(true);
        statusField.setPlaceholder("Pending...");
        binder.bind(statusField, HelloView.ContextObject::getStatusText, HelloView.ContextObject::setStatusText);

        H2 header = new H2("Survia");
        header.addClassNames(LumoUtility.Margin.Top.XLARGE, LumoUtility.Margin.Bottom.MEDIUM);
        add(header);
        add(new Paragraph("Open Source Intelligence (OSINT) Tool for the Surveillance of Austrian Politics"));

        UnorderedList unorderedList = new UnorderedList();
        unorderedList.add(new ListItem("Version: " + properties.getVersion()));
        unorderedList.add(new ListItem("API Version: " + properties.getApiVersion()));
        unorderedList.add(new ListItem("JWT Version: " + properties.getJwtVersion()));
        unorderedList.add(new ListItem("Keycloak Version: " + properties.getKeycloakVersion()));
        unorderedList.add(new ListItem("Vaadin Version: " + properties.getVaadinVersion()));
        add(unorderedList);

        add(new H3(new Text("Connection")));

        Button button = new Button("Check node status", LineAwesomeIcon.HEART.create());
        button.addClickListener(event -> checkNodeStatusManually());
        add(button);

        add(new Paragraph("Currently connected to Survia-Node " + properties.getNodeUrl() + "."));
        add(new Paragraph("The connection status to your Survia node is: "));
        add(statusField);

        setSizeFull();
    }

    private void updateUI() {
        binder.readBean(contextObject);
    }

    private void checkNodeStatusManually() {
        requestNodeStatus();
        updateUI();
    }

    @Scheduled(fixedRate = 5L, timeUnit = TimeUnit.MINUTES)
    public void checkNodeStatus() {
        requestNodeStatus(true);
        getUI().ifPresent(ui -> ui.access(this::updateUI));
    }

    private void requestNodeStatus() {
        requestNodeStatus(false);
    }

    private void requestNodeStatus(boolean scheduled) {
        Optional<ApplicationResponse> applicationResponse = Optional.empty();
        try {
            applicationResponse = Optional.of(statusController.status());
        } catch (Exception e) {
            errorHandlingController.handleApiException(e);
            contextObject.setStatusText("FAILED. Last status check: " + getFormattedTime());
        }

        applicationResponse.ifPresent(response -> contextObject.setStatusText(response.getApplicationStatus().toString() + ". Last status check: " + getFormattedTime()));
    }

    private String getFormattedTime() {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.GERMAN).withZone(ZoneId.systemDefault()).format(Instant.now());
    }

    private class ContextObject{
        String statusText;

        public String getStatusText() {
            return statusText;
        }

        public void setStatusText(String statusText) {
            this.statusText = statusText;
        }
    }

}
