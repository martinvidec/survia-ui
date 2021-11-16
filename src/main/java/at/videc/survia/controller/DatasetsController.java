package at.videc.survia.controller;

import at.videc.survia.controller.base.AuthenticatedApiController;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DatasetEntityControllerApi;
import org.openapitools.client.model.DatasetRequestBody;
import org.openapitools.client.model.EntityModelDataset;
import org.openapitools.client.model.PagedModelEntityModelDataset;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatasetsController extends AuthenticatedApiController<DatasetEntityControllerApi> {

    public int count(Integer page, Integer size) throws ApiException {
        DatasetEntityControllerApi api = getAuthenticatedApi();

        PagedModelEntityModelDataset pagedModelEntityModelDataset = api.getCollectionResourceDatasetGet1(page, size, null);
        return pagedModelEntityModelDataset.getPage().getTotalElements().intValue();
    }

    public List<EntityModelDataset> list(Integer page, Integer size, List<String> sort) throws ApiException {
        DatasetEntityControllerApi api = getAuthenticatedApi();

        PagedModelEntityModelDataset pagedModelEntityModelDataset = api.getCollectionResourceDatasetGet1(page, size, sort);

        return pagedModelEntityModelDataset.getEmbedded().getDatasets();
    }

    public void save(EntityModelDataset dataset) throws ApiException {
        DatasetEntityControllerApi api = getAuthenticatedApi();

        DatasetRequestBody datasetRequestBody = new DatasetRequestBody();
        if (dataset.getExternalId() != null && !dataset.getExternalId().isEmpty()) {
            datasetRequestBody.setId(Long.parseLong(dataset.getExternalId()));
        }
        datasetRequestBody.setName(dataset.getName());
        datasetRequestBody.setDescription(dataset.getDescription());
        datasetRequestBody.setOrganization(dataset.getOrganization());

        if (dataset.getExternalId() != null && !dataset.getExternalId().isEmpty()) {
            api.putItemResourceDatasetPut(dataset.getExternalId(), datasetRequestBody);
        } else {
            api.postCollectionResourceDatasetPost(datasetRequestBody);
        }
    }

    public void delete(EntityModelDataset entityModelDataset) throws ApiException {
        DatasetEntityControllerApi api = getAuthenticatedApi();
        api.deleteItemResourceDatasetDelete(entityModelDataset.getExternalId());
    }

    @Override
    protected DatasetEntityControllerApi getApi() {
        return new DatasetEntityControllerApi();
    }

    @Override
    protected ApiClient getApiClient(DatasetEntityControllerApi api) {
        return api.getApiClient();
    }
}
