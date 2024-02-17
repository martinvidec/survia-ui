package at.videc.survia.ui.controller;

import at.videc.survia.restclient.api.MeasurementUnitEntityControllerApi;
import at.videc.survia.restclient.model.*;
import at.videc.survia.ui.controller.base.BaseApiController;
import at.videc.survia.ui.controller.base.IGridController;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class MeasurementUnitsController extends BaseApiController<MeasurementUnitEntityControllerApi> implements IGridController<EntityModelMeasurementUnit> {

    public MeasurementUnitsController(
            MeasurementUnitEntityControllerApi measurementUnitEntityControllerApi
    ) {
        super(measurementUnitEntityControllerApi);
    }

    @Override
    public int count(Integer page, Integer size) {
        // TODO user a correct count method as api call
        PagedModelEntityModelMeasurementUnit pagedModelEntityModelMeasurementUnit = getApi().getCollectionResourceMeasurementunitGet1(page, size, null);
        return Optional.ofNullable(pagedModelEntityModelMeasurementUnit.getPage()).map(metadata -> Optional.ofNullable(metadata.getTotalElements()).orElse(0L).intValue()).orElse(0);
    }

    @Override
    public List<EntityModelMeasurementUnit> list(Integer page, Integer size, List<String> sort) {
        PagedModelEntityModelMeasurementUnit pagedModelEntityModelMeasurementUnit = getApi().getCollectionResourceMeasurementunitGet1(page, size, sort);
        return Optional.ofNullable(pagedModelEntityModelMeasurementUnit.getEmbedded()).orElse(new PagedModelEntityModelMeasurementUnitEmbedded().measurementUnits(Collections.emptyList())).getMeasurementUnits();
    }

    @Override
    public void save(EntityModelMeasurementUnit measurementUnit) {
        MeasurementUnitRequestBody measurementUnitRequestBody = new MeasurementUnitRequestBody();
        measurementUnitRequestBody.setName(measurementUnit.getName());
        measurementUnitRequestBody.setDescription(measurementUnit.getDescription());

        if (measurementUnit.getExternalId() != null && !measurementUnit.getExternalId().isEmpty()) {
            // update
            measurementUnitRequestBody.setId(Long.parseLong(measurementUnit.getExternalId()));
            getApi().putItemResourceMeasurementunitPut(measurementUnit.getExternalId(), measurementUnitRequestBody);
        } else {
            // create
            getApi().postCollectionResourceMeasurementunitPost(measurementUnitRequestBody);
        }
    }

    @Override
    public void delete(EntityModelMeasurementUnit entityModelMeasurementUnit) {
        getApi().deleteItemResourceMeasurementunitDelete(entityModelMeasurementUnit.getExternalId());
    }
}
