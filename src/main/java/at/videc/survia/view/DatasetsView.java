package at.videc.survia.view;

import at.videc.survia.controller.DatasetsController;
import at.videc.survia.controller.ErrorHandlingController;
import at.videc.survia.view.base.BaseView;
import at.videc.survia.view.forms.DatasetForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.SortOrder;
import com.vaadin.flow.router.Route;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.EntityModelDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Route(value = "datasets", layout = MainView.class)
public class DatasetsView extends BaseView {

    private final static Logger LOG = LoggerFactory.getLogger(DatasetsView.class);

    private Grid<EntityModelDataset> datasetGrid;

    private DatasetsController datasetsController;

    private ErrorHandlingController errorHandlingController;

    private Dialog dialog;

    @Autowired
    public DatasetsView(DatasetsController datasetsController, ErrorHandlingController errorHandlingController) {
        this.datasetsController = datasetsController;
        this.errorHandlingController = errorHandlingController;

        this.setSizeFull();

        add(new H3("Datasets View"));
        addTodos();
        addToolbar();
        addDatasetsGrid();
    }

    private void addDatasetsGrid() {
        DataProvider<EntityModelDataset, Void> provider = DataProvider.fromCallbacks(
                query -> {
                    List<String> sortOrders = new ArrayList<>();
                    for(SortOrder<String> queryOrder : query.getSortOrders()) {
                        String sortString = queryOrder.getSorted() + "," + (queryOrder.getDirection() == SortDirection.ASCENDING ? "asc" : "desc");
                        sortOrders.add(sortString);
                    }

                    try {
                        return datasetsController.list(query.getOffset(), query.getLimit(), sortOrders).stream();
                    } catch (ApiException e) {
                        errorHandlingController.handleApiException(e);
                        List<EntityModelDataset> emptyDatasetList = Collections.emptyList();
                        return emptyDatasetList.stream();
                    }
                },
                query -> {
                    try {
                        return datasetsController.count(query.getOffset(), query.getLimit());
                    } catch (ApiException e) {
                        errorHandlingController.handleApiException(e);
                        return 0;
                    }
                });

        datasetGrid = new Grid<>();
        datasetGrid.setDataProvider(provider);
        //datasetGrid.addColumn(EntityModelDataset::getLogo).setHeader("Logo");
        datasetGrid.addColumn(EntityModelDataset::getName).setHeader("Name").setSortProperty("name");
        datasetGrid.addColumn(EntityModelDataset::getDescription).setHeader("Description");
        datasetGrid.addColumn(EntityModelDataset::getOrganization).setHeader("Organization").setSortProperty("organization");
        datasetGrid.addComponentColumn(dataset -> createDeleteBtn(dataset));
        datasetGrid.addComponentColumn(dataset -> createEditBtn(dataset));
        datasetGrid.setSelectionMode(Grid.SelectionMode.NONE);

        // auto width
        datasetGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        // full height
        datasetGrid.setSizeFull();
        // enable multi sort
        datasetGrid.setMultiSort(true);

//        HeaderRow filterRow = datasetGrid.appendHeaderRow();
//
//
//        // first filter
//        TextField nameFilterField = new TextField();
//        nameFilterField.addValueChangeListener(event -> {
//           filterO
//        });


        add(datasetGrid);
    }

    private void addToolbar() {
        Button createDatasetBtn = new Button("create", new Icon(VaadinIcon.PLUS_CIRCLE));
        createDatasetBtn.addClickListener(event -> openCreateForm());

        Button reloadDatasetBtn = new Button("reload", new Icon(VaadinIcon.REFRESH));
        reloadDatasetBtn.addClickListener(event -> reloadDatasetGrid());

        TextField searchField = new TextField();
        searchField.setLabel("Search");
        searchField.setPlaceholder("Search");

        Button searchDatasetBtn = new Button("search", new Icon(VaadinIcon.SEARCH));
        reloadDatasetBtn.addClickListener(event -> search());

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.add(createDatasetBtn, reloadDatasetBtn, searchField, searchDatasetBtn);

        add(toolbar);
    }

    private void search() {
    }

    private void addTodos() {
        OrderedList todos = new OrderedList();
        todos.add(new ListItem("Add Form Element to create a Dataset"));
        todos.add(new ListItem("Add List View to display created Datasets"));
        todos.add(new ListItem("Add Form Element to update a Dataset from List View"));
        todos.add(new ListItem("Add Detail View to read a Dataset for read-only Access"));
        todos.add(new ListItem("Add Form Element to delete a Dataset"));

        add(todos);
    }

    private Component createDeleteBtn(EntityModelDataset dataset) {
        Button deleteBtn = new Button(new Icon(VaadinIcon.MINUS_CIRCLE), event -> {
            try {
                datasetsController.delete(dataset);
            } catch (ApiException e) {
                errorHandlingController.handleError(HttpStatus.resolve(e.getCode()), e.getResponseBody());
            }
            reloadDatasetGrid();
        });
        return deleteBtn;
    }

    private Component createEditBtn(EntityModelDataset dataset) {
        Button editBtn = new Button(new Icon(VaadinIcon.EDIT), event -> {
            openEditForm(dataset);
        });
        return editBtn;
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
            } catch (ApiException e) {
                errorHandlingController.handleError(HttpStatus.resolve(e.getCode()), e.getResponseBody());
            }
            dialog.close();
            reloadDatasetGrid();
        });
        datasetForm.addCancelListener(event -> dialog.close());

        dialog = new Dialog();
        dialog.add(datasetForm, datasetForm.getBtnLayout());
        dialog.open();
    }

    private void reloadDatasetGrid() {
        datasetGrid.getDataProvider().refreshAll();
    }
}
