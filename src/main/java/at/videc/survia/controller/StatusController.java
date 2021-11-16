package at.videc.survia.controller;

import at.videc.survia.controller.base.AuthenticatedApiController;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.StatusControllerApi;
import org.openapitools.client.model.ApplicationResponse;
import org.springframework.stereotype.Component;

@Component
public class StatusController extends AuthenticatedApiController<StatusControllerApi> {

    public ApplicationResponse status() throws ApiException {
        return getApi().status();
    }

    @Override
    protected StatusControllerApi getApi() {
        return new StatusControllerApi();
    }

    @Override
    protected ApiClient getApiClient(StatusControllerApi api) {
        return api.getApiClient();
    }
}
