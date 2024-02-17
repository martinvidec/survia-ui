package at.videc.survia.ui.views.codedvalues;

import at.videc.survia.restclient.model.EntityModelCodedValue;
import at.videc.survia.ui.views.base.BaseEditForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class CodedValuesForm extends BaseEditForm<EntityModelCodedValue> {

    private IntegerField codeField;

    private TextField valueField;

    private TextField externalIdField;

    @Override
    protected Binder<EntityModelCodedValue> createBinder() {
        return new Binder<>(EntityModelCodedValue.class);
    }

    @Override
    protected void initComponents() {
        externalIdField = new TextField();
        externalIdField.setLabel("External Id");
        externalIdField.setReadOnly(true);

        codeField = new IntegerField();
        codeField.setLabel("Code");
        codeField.setPlaceholder("Code of the value");

        valueField = new TextField();
        valueField.setLabel("Value");
        valueField.setPlaceholder("Value of the code");

        add(codeField, valueField);
    }

    @Override
    protected void addAdditionalConfirmButtons(HorizontalLayout layout) {
        // Nothing here...
    }

    @Override
    protected void initBinding(Binder<EntityModelCodedValue> binder) {
        binder.bind(externalIdField, EntityModelCodedValue::getExternalId, EntityModelCodedValue::setExternalId);
        binder.bind(codeField, EntityModelCodedValue::getCode, EntityModelCodedValue::setCode);
        binder.bind(valueField, EntityModelCodedValue::getValue, EntityModelCodedValue::setValue);
    }

    @Override
    protected EntityModelCodedValue createEntityModel() {
        return new EntityModelCodedValue();
    }
}
