package at.videc.survia.ui.views.codedvalues;

import at.videc.survia.restclient.model.EntityModelCodedValue;
import at.videc.survia.ui.controller.CodedValuesController;
import at.videc.survia.ui.controller.ErrorHandlingController;
import at.videc.survia.ui.views.MainLayout;
import at.videc.survia.ui.views.base.BaseEditForm;
import at.videc.survia.ui.views.base.BaseGridView;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Coded Values")
@Route(value = "coded-values", layout = MainLayout.class)
@RolesAllowed("USER")
public class CodedValuesView extends BaseGridView<EntityModelCodedValue> {

    public CodedValuesView(
            CodedValuesController codedValuesController,
            ErrorHandlingController errorHandlingController
    ) {
        super(codedValuesController, errorHandlingController);
    }

    @Override
    protected BaseEditForm<EntityModelCodedValue> createEntityModelForm() {
        return new CodedValuesForm();
    }

    @Override
    protected List<IColumnModel<EntityModelCodedValue>> createColumnModelList() {
        final List<IColumnModel<EntityModelCodedValue>> columnModelList = new ArrayList<>();
        columnModelList.add(createColumnModel(entityModelCodedValue -> entityModelCodedValue.getCode().toString(), "Code", "code"));
        columnModelList.add(createColumnModel(EntityModelCodedValue::getValue, "Value", "value"));
        return columnModelList;
    }

    @Override
    protected EntityModelCodedValue createNewEntityModel() {
        return new EntityModelCodedValue();
    }


}
