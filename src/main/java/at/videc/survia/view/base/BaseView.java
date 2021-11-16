package at.videc.survia.view.base;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public abstract class BaseView extends VerticalLayout {

    public BaseView() {
        setClassName("survia-baseview survia-" + this.getClass().getSimpleName().toLowerCase());
    }

    /**
     * Since @{@link com.vaadin.flow.component.page.Push} is enabled in {@link at.videc.survia.view.MainView} this Method can be used
     * to update UI Components.
     *
     * @param r the method which should run on the UI
     */
    protected void updateUI(Runnable r) {
        getUI().ifPresent(ui -> {
            ui.access(() -> {
                r.run();
            });
        });
    }
}
