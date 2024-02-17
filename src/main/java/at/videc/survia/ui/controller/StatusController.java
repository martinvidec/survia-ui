package at.videc.survia.ui.controller;

import at.videc.survia.ui.controller.base.BaseApiController;
import at.videc.survia.restclient.api.StatusControllerApi;
import at.videc.survia.restclient.model.ApplicationResponse;
import org.springframework.stereotype.Component;

@Component
public class StatusController extends BaseApiController<StatusControllerApi> {

    public StatusController(
            StatusControllerApi api
    ) {
        super(api);
    }

    public ApplicationResponse status() {
        return getApi().status();
    }

}
