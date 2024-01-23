package at.videc.survia.ui.views.datasets;

import at.videc.survia.ui.controller.DatasetsController;
import at.videc.survia.ui.controller.ErrorHandlingController;
import at.videc.survia.restclient.model.EntityModelDataset;
import at.videc.survia.ui.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.SortOrder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@PageTitle("Datasets")
@Route(value = "datasets", layout = MainLayout.class)
@RolesAllowed("USER")
public class DatasetsView extends VerticalLayout {

    private final DatasetsController datasetsController;

    private final ErrorHandlingController errorHandlingController;

    private Grid<EntityModelDataset> datasetGrid;

    private Dialog dialog;

    public DatasetsView(
            DatasetsController datasetsController,
            ErrorHandlingController errorHandlingController
    ) {
        this.datasetsController = datasetsController;
        this.errorHandlingController = errorHandlingController;

        setSpacing(false);
        setPadding(false);

        add(createMenuBar());

        datasetGrid = createDatasetGrid();
        add(datasetGrid);

        setSizeFull();
    }

    private Component createMenuBar() {
        MenuBar menuBar = new MenuBar();

        menuBar.addItem("New...", event -> openCreateForm());
        menuBar.addItem("Reload", event -> datasetGrid.getDataProvider().refreshAll());

        TextField searchField = new TextField();
        searchField.setLabel("Search");
        searchField.setPlaceholder("Search");

        Button searchDatasetBtn = new Button("search", new Icon(VaadinIcon.SEARCH));
        searchDatasetBtn.addClickListener(event -> search());

        return menuBar;
    }

    private Grid<EntityModelDataset> createDatasetGrid() {
        Grid<EntityModelDataset> datasetGrid = new Grid<>();
        datasetGrid.setDataProvider(createDatasetProvider());
        //datasetGrid.addColumn(EntityModelDataset::getLogo).setHeader("Logo");
        datasetGrid.addColumn(EntityModelDataset::getName).setHeader("Name").setSortProperty("name");
        datasetGrid.addColumn(EntityModelDataset::getDescription).setHeader("Description");
        datasetGrid.addColumn(EntityModelDataset::getOrganization).setHeader("Organization").setSortProperty("organization");
        datasetGrid.addComponentColumn(this::createEditBtn).setAutoWidth(true).setFlexGrow(0);
        datasetGrid.addComponentColumn(this::createDeleteBtn).setAutoWidth(true).setFlexGrow(0);
        datasetGrid.setSelectionMode(Grid.SelectionMode.NONE);

        // auto width
        datasetGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        // full height
        datasetGrid.setSizeFull();
        // enable multi sort
        datasetGrid.setMultiSort(true);
        // remove borders
        datasetGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        return datasetGrid;
    }

    private Component createEditBtn(EntityModelDataset dataset) {
        return new Button(LineAwesomeIcon.EDIT.create(), event -> {
            openEditForm(dataset);
        });
    }

    private Component createDeleteBtn(EntityModelDataset dataset) {
        Button deleteBtn = new Button(LineAwesomeIcon.TRASH_SOLID.create(), event -> {
            try {
                datasetsController.delete(dataset);
            } catch (Exception e) {
                errorHandlingController.handleApiException(e);
            }
            datasetGrid.getDataProvider().refreshAll();
        });
        return deleteBtn;
    }

    private DataProvider<EntityModelDataset, ?> createDatasetProvider() {
        return DataProvider.fromCallbacks(
                query -> {
                    List<String> sortOrders = new ArrayList<>();
                    for (SortOrder<String> queryOrder : query.getSortOrders()) {
                        String sortString = queryOrder.getSorted() + "," + (queryOrder.getDirection() == SortDirection.ASCENDING ? "asc" : "desc");
                        sortOrders.add(sortString);
                    }

                    try {
                        return datasetsController.list(query.getOffset(), query.getLimit(), sortOrders).stream();
                    } catch (Exception e) {
                        errorHandlingController.handleApiException(e);
                        List<EntityModelDataset> emptyDatasetList = Collections.emptyList();
                        return emptyDatasetList.stream();
                    }
                },
                query -> {
                    try {
                        return datasetsController.count(query.getOffset(), query.getLimit());
                    } catch (Exception e) {
                        errorHandlingController.handleApiException(e);
                        return 0;
                    }
                });
    }

    private void openCreateForm() {
        DatasetForm datasetForm = new DatasetForm();
        datasetForm.setDataset(new EntityModelDataset());
        openForm(datasetForm);
    }

    private void openEditForm(EntityModelDataset dataset) {
        DatasetForm datasetForm = new DatasetForm();
        datasetForm.setDataset(dataset);
        openForm(datasetForm);
    }

    private void openForm(DatasetForm datasetForm) {
        datasetForm.addSaveListener(event -> {
            try {
                datasetsController.save(datasetForm.getDataset());
            } catch (ValidationException e) {
                errorHandlingController.handleUnexpectedError(e);
            } catch (Exception e) {
                errorHandlingController.handleApiException(e);
            }
            dialog.close();
            datasetGrid.getDataProvider().refreshAll();
        });
        datasetForm.addCancelListener(event -> dialog.close());

        dialog = new Dialog();
        dialog.add(datasetForm, datasetForm.getBtnLayout());
        dialog.open();
    }

    private void search() {
    }

}
