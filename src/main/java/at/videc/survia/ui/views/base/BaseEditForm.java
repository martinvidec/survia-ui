package at.videc.survia.ui.views.base;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

abstract public class BaseEditForm<T> extends FormLayout {

    private Binder<T> binder;

    private HorizontalLayout confirmButtonLayout;

    private Button saveBtn;
    private Button cancelBtn;

    public BaseEditForm() {
        _init();
    }

    public HorizontalLayout getBtnLayout() {
        return confirmButtonLayout;
    }

    public void setEntityModel(T entityModel) {
        binder.readBean(entityModel);
        afterBinderRead(entityModel);
    }

    public T getEntityModel() throws ValidationException {
        T entityModel = createEntityModel();
        binder.writeBean(entityModel);
        afterBinderWrite(entityModel);
        return entityModel;
    }

    protected abstract Binder<T> createBinder();

    /**
     * Initializes all required components
     */
    protected abstract void initComponents();

    protected abstract void addAdditionalConfirmButtons(HorizontalLayout layout);

    protected abstract void initBinding(Binder<T> binder);

    protected abstract T createEntityModel();

    /**
     * Override this method if needed
     */
    protected void afterBinderRead(T entityModel) {
        // override this method if needed
    }

    /**
     * Override this method if needed
     */
    protected void afterBinderWrite(T entityModel) {
        // override this method if needed
    }

    private void _init() {
        _initComponents();
        _initBinding();
        _initConfirmButtons();
    }

    private void _initComponents() {
        initComponents();
    }

    private void _initBinding() {
        binder = createBinder();
        initBinding(binder);
    }

    private void _initConfirmButtons() {
        confirmButtonLayout = new HorizontalLayout();

        addAdditionalConfirmButtons(confirmButtonLayout);

        saveBtn = new Button("Save");
        cancelBtn = new Button("Cancel");
        confirmButtonLayout.add(saveBtn, cancelBtn);
    }

    public void addSaveListener(ComponentEventListener<ClickEvent<Button>> listener) {
        saveBtn.addClickListener(listener);
    }

    public void addCancelListener(ComponentEventListener<ClickEvent<Button>> listener) {
        cancelBtn.addClickListener(listener);
    }

}
