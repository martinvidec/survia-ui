package at.videc.survia.controller;

import at.videc.survia.view.DatasetsView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.openapitools.client.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ErrorHandlingController {

    private final static Logger LOG = LoggerFactory.getLogger(ErrorHandlingController.class);

    public void handleApiException(ApiException e) {
        handleError(HttpStatus.resolve(e.getCode()), e.getResponseBody());
    }

    public void handleError(HttpStatus httpStatus, String responseBody) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        dialog.setWidth("400");

        Button confirmButton = new Button("Schliessen", event -> {
            dialog.close();
        });
        confirmButton.addThemeVariants(ButtonVariant.MATERIAL_OUTLINED);

        Text messageText = new Text("Fehler beim Aufrufen der Schnittstelle.");

        String prettyResponseBody;
        if(responseBody != null && !responseBody.equals("")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(responseBody);
            prettyResponseBody = gson.toJson(jsonElement);
        } else {
            prettyResponseBody = "Keine Response Vorhanden.";
        }

        Paragraph paragraph = new Paragraph(prettyResponseBody);
        paragraph.setWidth(dialog.getWidth());

        Details details;
        if (httpStatus != null) {
            details = new Details(httpStatus.toString(), paragraph);
        } else {
            details = new Details("UNKNOWN", paragraph);
        }
        details.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth(dialog.getWidth());
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH, details);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, confirmButton);
        verticalLayout.add(messageText, details, confirmButton);

        dialog.add(verticalLayout);
        dialog.open();
    }

    public void handleUnexpectedError(Exception e) {
        Dialog dialog = new Dialog();
        dialog.add(new Text(e.getMessage()));
        dialog.open();
        LOG.error(e.getMessage(), e);
    }

}
