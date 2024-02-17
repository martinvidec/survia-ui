package at.videc.survia.ui.controller;

import com.google.gson.*;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

/**
 * This class is used to handle errors.
 * <br/>
 * Use this class to handle errors that occur when calling the API. This class will display a dialog with the error message and the response body.
 * <br/>
 * Don't inject this class into other controllers. Instead, use it from inside the views.
 */
@Component
public class ErrorHandlingController {

    private final static Logger LOG = LoggerFactory.getLogger(ErrorHandlingController.class);
    public static final String FEHLER_BEIM_AUFRUFEN_DER_SCHNITTSTELLE = "Fehler beim Aufrufen der Schnittstelle.";
    public static final String UNKNOWN = "UNKNOWN";

    public void handleApiException(Exception e) {
//        handleError(HttpStatus.resolve(e.getCode()), e.getResponseBody());
        if(e instanceof HttpClientErrorException clientErrorException) {
            handleError(clientErrorException.getStatusCode(), clientErrorException.getResponseBodyAsString());
        } else {
            handleError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public void handleError(HttpStatusCode httpStatus, String responseBody) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        dialog.setWidth("400");

        Button confirmButton = new Button("Schliessen", event -> dialog.close());
        confirmButton.addThemeVariants(ButtonVariant.MATERIAL_OUTLINED);

        Text messageText = new Text(FEHLER_BEIM_AUFRUFEN_DER_SCHNITTSTELLE);

        String prettyResponseBody;
        if(responseBody != null && !responseBody.isEmpty()) {
            try {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonStreamParser jsonStreamParser = new JsonStreamParser(responseBody);
                JsonElement jsonElement = jsonStreamParser.next();
                prettyResponseBody = gson.toJson(jsonElement);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                prettyResponseBody = responseBody;
            }
        } else {
            prettyResponseBody = responseBody;
        }

        Paragraph paragraph = new Paragraph(prettyResponseBody);
        paragraph.setWidth(dialog.getWidth());

        Details details;
        if (httpStatus != null) {
            details = new Details(httpStatus.toString(), paragraph);
        } else {
            details = new Details(UNKNOWN, paragraph);
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
