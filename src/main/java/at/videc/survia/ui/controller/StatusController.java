package at.videc.survia.ui.controller;

import at.videc.survia.ui.configuration.properties.AppProperties;
import at.videc.survia.ui.controller.base.AuthenticatedApiController;
import at.videc.survia.restclient.api.StatusControllerApi;
import at.videc.survia.restclient.model.ApplicationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatusController extends AuthenticatedApiController<StatusControllerApi> {

    private final AppProperties appProperties;

    @Autowired
    private final StatusControllerApi api;

    public StatusController(
            StatusControllerApi api,
            AppProperties appProperties
    ) {
        this.api = api;
        this.appProperties = appProperties;
    }

    public ApplicationResponse status() {
        return api.status();
    }

    @Override
    protected StatusControllerApi getApi() {
        return api;
    }
}
