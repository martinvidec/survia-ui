package at.videc.survia.ui.controller.base;

public abstract class BaseApiController<T> {

    private final T api;

    protected BaseApiController(T api) {
        this.api = api;
    }

    protected T getApi() { return api; }
}
