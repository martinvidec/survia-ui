package at.videc.survia.ui.controller;

import at.videc.survia.ui.configuration.properties.AppProperties;
import at.videc.survia.ui.controller.base.AuthenticatedApiController;
import at.videc.survia.restclient.api.DatasetEntityControllerApi;
import at.videc.survia.restclient.model.DatasetRequestBody;
import at.videc.survia.restclient.model.EntityModelDataset;
import at.videc.survia.restclient.model.PagedModelEntityModelDataset;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatasetsController extends AuthenticatedApiController<DatasetEntityControllerApi> {

    private final AppProperties appProperties;

    final DatasetEntityControllerApi api;

    public DatasetsController(
            DatasetEntityControllerApi datasetEntityControllerApi,
            AppProperties appProperties
    ) {
        this.appProperties = appProperties;
        this.api = datasetEntityControllerApi;
    }

    public int count(Integer page, Integer size) {
        PagedModelEntityModelDataset pagedModelEntityModelDataset = api.getCollectionResourceDatasetGet1(page, size, null);
        return pagedModelEntityModelDataset.getPage().getTotalElements().intValue();
    }

    public List<EntityModelDataset> list(Integer page, Integer size, List<String> sort) {
        PagedModelEntityModelDataset pagedModelEntityModelDataset = api.getCollectionResourceDatasetGet1(page, size, sort);
        return pagedModelEntityModelDataset.getEmbedded().getDatasets();
    }

    public void save(EntityModelDataset dataset) {
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

    public void delete(EntityModelDataset entityModelDataset) {
        api.deleteItemResourceDatasetDelete(entityModelDataset.getExternalId());
    }

    @Override
    protected DatasetEntityControllerApi getApi() {
        return api;
    }
}
