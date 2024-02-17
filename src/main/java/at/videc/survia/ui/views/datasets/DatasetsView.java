package at.videc.survia.ui.views.datasets;

import at.videc.survia.restclient.model.EntityModelDataset;
import at.videc.survia.ui.controller.DatasetsController;
import at.videc.survia.ui.controller.ErrorHandlingController;
import at.videc.survia.ui.views.MainLayout;
import at.videc.survia.ui.views.base.BaseEditForm;
import at.videc.survia.ui.views.base.BaseGridView;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Datasets")
@Route(value = "datasets", layout = MainLayout.class)
@RolesAllowed("USER")
public class DatasetsView extends BaseGridView<EntityModelDataset> {

    public DatasetsView(
            DatasetsController datasetsController,
            ErrorHandlingController errorHandlingController
    ) {
        super(datasetsController, errorHandlingController);
    }

    @Override
    protected EntityModelDataset createNewEntityModel() {
        return new EntityModelDataset();
    }

    @Override
    protected BaseEditForm<EntityModelDataset> createEntityModelForm() {
        return new DatasetForm();
    }

    @Override
    protected List<IColumnModel<EntityModelDataset>> createColumnModelList() {
        List<IColumnModel<EntityModelDataset>> columnModelList = new ArrayList<>();
        columnModelList.add(createComponentColumnModel(this::createLogoImage));
        columnModelList.add(createColumnModel(EntityModelDataset::getName, "Name", "name"));
        columnModelList.add(createColumnModel(EntityModelDataset::getDescription, "Description", "description"));
        columnModelList.add(createColumnModel(EntityModelDataset::getOrganization, "Organization", "organization"));
        return columnModelList;
    }

    private Image createLogoImage(EntityModelDataset entityModelDataset) {
        if (entityModelDataset.getLogo() == null || entityModelDataset.getLogo().length == 0) {
            return null;
        }

        Image image = new Image();
        image.getClassNames().add(LumoUtility.IconSize.LARGE);
        image.getClassNames().add(LumoUtility.BorderRadius.LARGE);
        image.setSrc(new String(entityModelDataset.getLogo(), StandardCharsets.UTF_8));
        return image;
    }

//    private static Renderer<EntityModelDataset> createAvatarRenderer() {
//        return LitRenderer.<EntityModelDataset> of(
//                        "<vaadin-avatar img=\"${item.pictureUrl}\" name=\"${item.fullName}\" alt=\"User avatar\"></vaadin-avatar>")
//                .withProperty("pictureUrl", EntityModelDataset::getLogo);
//    }

}
