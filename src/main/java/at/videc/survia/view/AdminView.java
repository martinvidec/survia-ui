package at.videc.survia.view;

import at.videc.survia.config.AppProperties;
import at.videc.survia.controller.ErrorHandlingController;
import at.videc.survia.controller.StatusController;
import at.videc.survia.view.base.BaseView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.ApplicationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;


@Route(value = "admin", layout = MainView.class)
@EnableScheduling
@Configuration
public class AdminView extends BaseView {

    private final static Logger LOG = LoggerFactory.getLogger(AdminView.class);

    private AppProperties properties;
    private ErrorHandlingController errorHandlingController;
    private StatusController statusController;
    private ContextObject contextObject;
    private Binder<ContextObject> binder;

    @Autowired
    public AdminView(AppProperties properties, StatusController statusController, ErrorHandlingController errorHandlingController) {
        this.properties = properties;
        this.errorHandlingController = errorHandlingController;
        this.statusController = statusController;
        contextObject = new ContextObject();
        binder = new Binder<>(ContextObject.class);

        TextField statusField = new TextField();
        statusField.setWidth("100%");
        statusField.setReadOnly(true);
        statusField.setPlaceholder("Pending...");
        binder.bind(statusField, ContextObject::getStatusText, ContextObject::setStatusText);

        Button button = new Button("Check node status", new Icon(VaadinIcon.HEART));
        button.addClickListener(event -> checkNodeStatusManually());

        add(new H3(new Text("Version Information")));

        UnorderedList unorderedList = new UnorderedList();
        unorderedList.add(new ListItem("Version: " + properties.getVersion()));
        unorderedList.add(new ListItem("API Version: " + properties.getApiVersion()));
        unorderedList.add(new ListItem("JWT Version: " + properties.getJwtVersion()));

        add(unorderedList);

        add(new H3(new Text("Connection")));
        add(button);
        add(new Paragraph("Currently connected to Survia-Node " + properties.getNodeUrl() + "."));
        add(new Paragraph("The connection status to your Survia node is: "));
        add(statusField);
    }

    private void checkNodeStatusManually() {
        requestNodeStatus();
        binder.readBean(contextObject);
    }

//    @Scheduled(fixedRateString = "#{@appProperties.getHeartbeatInterval()}")
    @Scheduled(fixedRate = 30000L)
    public void checkNodeStatus() {
        requestNodeStatus();
        updateUI(() -> {
            binder.readBean(contextObject);
        });
    }

    private void requestNodeStatus() {
        try {
            ApplicationResponse applicationResponse = statusController.status();
            contextObject.setStatusText(applicationResponse.getApplicationStatus().toString() + ". Last status check: " + getFormattedTime());
        } catch (ApiException e) {
            contextObject.setStatusText(e.getCode() != 0 ? HttpStatus.resolve(e.getCode()).toString() : "Connection failure. Last status check: " + getFormattedTime());
        } catch (Exception e) {
            // TODO create general Exception View/Dialog
            contextObject.setStatusText((e.getMessage() != null ? e.getMessage() : e.getCause().getMessage()) + " Last status check: " + getFormattedTime());
        }
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
