package at.videc.survia.ui.controller;

import at.videc.survia.restclient.model.PagedModelEntityModelDatasetEmbedded;
import at.videc.survia.ui.controller.base.BaseApiController;
import at.videc.survia.restclient.api.DatasetEntityControllerApi;
import at.videc.survia.restclient.model.DatasetRequestBody;
import at.videc.survia.restclient.model.EntityModelDataset;
import at.videc.survia.restclient.model.PagedModelEntityModelDataset;
import at.videc.survia.ui.controller.base.IGridController;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class DatasetsController extends BaseApiController<DatasetEntityControllerApi> implements IGridController<EntityModelDataset> {

    public DatasetsController(
            DatasetEntityControllerApi datasetEntityControllerApi
    ) {
        super(datasetEntityControllerApi);
    }

    @Override
    public int count(Integer page, Integer size) {
        PagedModelEntityModelDataset pagedModelEntityModelDataset = getApi().getCollectionResourceDatasetGet1(page, size, null);
        return Optional.ofNullable(pagedModelEntityModelDataset.getPage()).map(metadata -> Optional.ofNullable(metadata.getTotalElements()).orElse(0L).intValue()).orElse(0);
    }

    @Override
    public List<EntityModelDataset> list(Integer page, Integer size, List<String> sort) {
        PagedModelEntityModelDataset pagedModelEntityModelDataset = getApi().getCollectionResourceDatasetGet1(page, size, sort);
        return Optional.ofNullable(pagedModelEntityModelDataset.getEmbedded()).orElse(new PagedModelEntityModelDatasetEmbedded().datasets(Collections.emptyList())).getDatasets();
    }

    @Override
    public void save(EntityModelDataset dataset) {
        DatasetRequestBody datasetRequestBody = new DatasetRequestBody();
        if (dataset.getExternalId() != null && !dataset.getExternalId().isEmpty()) {
            datasetRequestBody.setId(Long.parseLong(dataset.getExternalId()));
        }
        datasetRequestBody.setName(dataset.getName());
        datasetRequestBody.setDescription(dataset.getDescription());
        datasetRequestBody.setOrganization(dataset.getOrganization());
        if(dataset.getLogo() != null && dataset.getLogo().length > 0) {
            datasetRequestBody.setLogo(dataset.getLogo());
        }

        if (dataset.getExternalId() != null && !dataset.getExternalId().isEmpty()) {
            getApi().putItemResourceDatasetPut(dataset.getExternalId(), datasetRequestBody);
        } else {
            getApi().postCollectionResourceDatasetPost(datasetRequestBody);
        }
    }

    @Override
    public void delete(EntityModelDataset entityModelDataset) {
        getApi().deleteItemResourceDatasetDelete(entityModelDataset.getExternalId());
    }

}
