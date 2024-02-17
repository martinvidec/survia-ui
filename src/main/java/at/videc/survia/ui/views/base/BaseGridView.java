package at.videc.survia.ui.views.base;

import at.videc.survia.ui.controller.ErrorHandlingController;
import at.videc.survia.ui.controller.base.IGridController;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.SortOrder;
import com.vaadin.flow.function.ValueProvider;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class BaseGridView<T> extends VerticalLayout {

    private Grid<T> grid;

    private final IGridController<T> gridController;

    private final ErrorHandlingController errorHandlingController;

    protected BaseGridView(IGridController<T> gridController, ErrorHandlingController errorHandlingController) {
        this.errorHandlingController = errorHandlingController;
        this.gridController = gridController;
        init();
    }

    protected abstract T createNewEntityModel();

    protected abstract BaseEditForm<T> createEntityModelForm();

    protected abstract List<IColumnModel<T>> createColumnModelList();

    protected DataProvider<T, Void> createDataProvider() {
        return DataProvider.fromCallbacks(
                query -> {
                    List<String> sortOrders = new ArrayList<>();
                    for (SortOrder<String> queryOrder : query.getSortOrders()) {
                        String sortString = queryOrder.getSorted() + "," + (queryOrder.getDirection() == SortDirection.ASCENDING ? "asc" : "desc");
                        sortOrders.add(sortString);
                    }

                    try {
                        return gridController.list(query.getOffset(), query.getLimit(), sortOrders).stream();
                    } catch (Exception e) {
                        errorHandlingController.handleApiException(e);
                        List<T> entityModelCodedValueList = Collections.emptyList();
                        return entityModelCodedValueList.stream();
                    }
                },
                query -> {
                    try {
                        return gridController.count(query.getOffset(), query.getLimit());
                    } catch (Exception e) {
                        errorHandlingController.handleApiException(e);
                        return 0;
                    }
                });
    }

    private void init() {
        initMenuBar();
        initGrid();

        setSpacing(false);
        setPadding(false);
        setSizeFull();
    }

    private void initMenuBar() {
        MenuBar menuBar = new MenuBar();

        menuBar.addItem("New...", event -> openCreateForm());
        menuBar.addItem("Reload", event -> grid.getDataProvider().refreshAll());

        add(menuBar);
    }

    private void initGrid() {
        grid = new Grid<>();

        grid.setDataProvider(createDataProvider());

        // add custom columns
        createColumnModelList().forEach(columnModel -> columnModel.addToGrid(grid));

        // add standard component columns
        grid.addComponentColumn(this::createEditBtn).setAutoWidth(true).setFlexGrow(0);
        grid.addComponentColumn(this::createDeleteBtn).setAutoWidth(true).setFlexGrow(0);


        // auto width
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        // full height
        grid.setSizeFull();
        // enable multi sort
        grid.setMultiSort(true);
        // remove borders
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        // selectionMode
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        add(grid);
    }

    private Component createEditBtn(T entityModel) {
        return new Button(LineAwesomeIcon.EDIT.create(), event -> openEditForm(entityModel));
    }

    private Component createDeleteBtn(T entityModel) {
        return new Button(LineAwesomeIcon.TRASH_SOLID.create(), event -> {
            try {
                gridController.delete(entityModel);
                // TODO event or callback here...?
            } catch (Exception e) {
                errorHandlingController.handleApiException(e);
            }
            grid.getDataProvider().refreshAll();
        });
    }

    private void openCreateForm() {
        BaseEditForm<T> form = createEntityModelForm();
        form.setEntityModel(createNewEntityModel());
        openForm(form);
    }

    private void openEditForm(T entityModel) {
        BaseEditForm<T> form = createEntityModelForm();
        form.setEntityModel(entityModel);
        openForm(form);
    }

    private void openForm(BaseEditForm<T> form) {
        final Dialog dialog = new Dialog();
        this.add(dialog);

        form.addSaveListener(event -> {
            try {
                gridController.save(form.getEntityModel());
                // TODO event or callback here...?
            } catch (ValidationException e) {
                errorHandlingController.handleUnexpectedError(e);
            } catch (Exception e) {
                errorHandlingController.handleApiException(e);
            }
            dialog.close();
            this.remove(dialog);

            grid.getDataProvider().refreshAll();
        });
        form.addCancelListener(event -> dialog.close());

        dialog.add(form, form.getBtnLayout());
        dialog.open();
    }

    protected IColumnModel<T> createColumnModel(ValueProvider<T, String> valueProvider, String header, String sortProperty) {
        return new DefaultColumnModel(valueProvider, header, sortProperty);
    }

    protected IColumnModel<T> createColumnModel(ValueProvider<T, String> valueProvider, String header) {
        return createColumnModel(valueProvider, header, null);
    }

    protected IColumnModel<T> createComponentColumnModel(ValueProvider<T, Component> componentProvider) {
        return new ComponentColumnModel(componentProvider);
    }

    protected interface IColumnModel<T> {
        void addToGrid(Grid<T> grid);
    }

    protected class DefaultColumnModel implements IColumnModel<T> {
        private final String header;
        private final String sortProperty;
        private final ValueProvider<T, String> valueProvider;

        private DefaultColumnModel(ValueProvider<T, String> valueProvider, String header, String sortProperty) {
            this.header = header;
            this.sortProperty = sortProperty;
            this.valueProvider = valueProvider;
        }

        @Override
        public void addToGrid(Grid<T> grid) {
            Grid.Column<T> column = grid.addColumn(valueProvider);
            Optional.ofNullable(header).ifPresent(column::setHeader);
            Optional.ofNullable(sortProperty).ifPresent(column::setSortProperty);
        }
    }

    protected class ComponentColumnModel implements IColumnModel<T> {
        private final ValueProvider<T, Component> componentProvider;

        private ComponentColumnModel(ValueProvider<T, Component> componentProvider) {
            this.componentProvider = componentProvider;
        }

        @Override
        public void addToGrid(Grid<T> grid) {
            grid.addComponentColumn(componentProvider).setAutoWidth(true).setFlexGrow(0);
        }
    }

}
