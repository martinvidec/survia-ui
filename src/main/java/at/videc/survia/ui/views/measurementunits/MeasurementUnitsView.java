package at.videc.survia.ui.views.measurementunits;

import at.videc.survia.restclient.model.EntityModelMeasurementUnit;
import at.videc.survia.ui.controller.ErrorHandlingController;
import at.videc.survia.ui.controller.MeasurementUnitsController;
import at.videc.survia.ui.views.MainLayout;
import at.videc.survia.ui.views.base.BaseEditForm;
import at.videc.survia.ui.views.base.BaseGridView;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Measurement Units")
@Route(value = "measurement-units", layout = MainLayout.class)
@RolesAllowed("USER")
public class MeasurementUnitsView extends BaseGridView<EntityModelMeasurementUnit> {

    public MeasurementUnitsView(
            MeasurementUnitsController measurementUnitsController,
            ErrorHandlingController errorHandlingController
    ) {
        super(measurementUnitsController, errorHandlingController);
    }

    @Override
    protected EntityModelMeasurementUnit createNewEntityModel() {
        return new EntityModelMeasurementUnit();
    }

    @Override
    protected BaseEditForm<EntityModelMeasurementUnit> createEntityModelForm() {
        return new MeasurementUnitForm();
    }

    @Override
    protected List<IColumnModel<EntityModelMeasurementUnit>> createColumnModelList() {
        final List<IColumnModel<EntityModelMeasurementUnit>> columnModelList = new ArrayList<>();
        columnModelList.add(createColumnModel(EntityModelMeasurementUnit::getName, "Name", "name"));
        columnModelList.add(createColumnModel(EntityModelMeasurementUnit::getDescription, "Description"));
        return columnModelList;
    }

}
