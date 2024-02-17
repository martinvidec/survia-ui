package at.videc.survia.ui.views.measurementunits;

import at.videc.survia.restclient.model.EntityModelMeasurementUnit;
import at.videc.survia.ui.views.base.BaseEditForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeasurementUnitForm extends BaseEditForm<EntityModelMeasurementUnit> {

    private final static Logger LOG = LoggerFactory.getLogger(MeasurementUnitForm.class);

    private TextField nameField;
    private TextField descriptionField;
    private TextField externalIdField;

    @Override
    protected Binder<EntityModelMeasurementUnit> createBinder() {
        return new Binder<>(EntityModelMeasurementUnit.class);
    }

    @Override
    protected void initComponents() {
        externalIdField = new TextField();
        externalIdField.setLabel("External Id");
        externalIdField.setReadOnly(true);

        nameField = new TextField();
        nameField.setLabel("Name");
        nameField.setPlaceholder("Unit name (e.g. Meter)");

        descriptionField = new TextField();
        descriptionField.setLabel("Description");
        descriptionField.setPlaceholder("Unit description (e.g. Distance)");

        add(nameField, descriptionField);
    }

    @Override
    protected void addAdditionalConfirmButtons(HorizontalLayout layout) {
        // Nothing here...
    }

    @Override
    protected void initBinding(Binder<EntityModelMeasurementUnit> binder) {
        binder.bind(externalIdField, EntityModelMeasurementUnit::getExternalId, EntityModelMeasurementUnit::setExternalId);
        binder.bind(nameField, EntityModelMeasurementUnit::getName, EntityModelMeasurementUnit::setName);
        binder.bind(descriptionField, EntityModelMeasurementUnit::getDescription, EntityModelMeasurementUnit::setDescription);
    }

    @Override
    protected EntityModelMeasurementUnit createEntityModel() {
        return null;
    }

}
