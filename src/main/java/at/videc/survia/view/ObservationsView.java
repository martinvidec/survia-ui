package at.videc.survia.view;

import at.videc.survia.config.AppProperties;
import at.videc.survia.controller.ErrorHandlingController;
import at.videc.survia.view.base.BaseView;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


@Route(value = "observations", layout = MainView.class)
public class ObservationsView extends BaseView {

    private final static Logger LOG = LoggerFactory.getLogger(ObservationsView.class);

    private AppProperties properties;
    private ErrorHandlingController errorHandlingController;

    @Autowired
    public ObservationsView(AppProperties properties, ErrorHandlingController errorHandlingController) {
        this.properties = properties;
        this.errorHandlingController = errorHandlingController;

        add(new H3("Observations View"));
    }
}
