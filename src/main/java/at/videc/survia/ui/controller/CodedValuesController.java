package at.videc.survia.ui.controller;

import at.videc.survia.restclient.api.CodedValueEntityControllerApi;
import at.videc.survia.restclient.model.*;
import at.videc.survia.ui.controller.base.BaseApiController;
import at.videc.survia.ui.controller.base.IGridController;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class CodedValuesController  extends BaseApiController<CodedValueEntityControllerApi> implements IGridController<EntityModelCodedValue> {

    public CodedValuesController(
            CodedValueEntityControllerApi codedValueEntityControllerApi
    ) {
        super(codedValueEntityControllerApi);
    }

    @Override
    public int count(Integer page, Integer size) {
        // TODO user a correct count method as api call
        PagedModelEntityModelCodedValue pagedModelEntityModelCodedValue = getApi().getCollectionResourceCodedvalueGet1(page, size, null);
        return Optional.ofNullable(pagedModelEntityModelCodedValue.getPage()).map(metadata -> Optional.ofNullable(metadata.getTotalElements()).orElse(0L).intValue()).orElse(0);
    }

    @Override
    public List<EntityModelCodedValue> list(Integer page, Integer size, List<String> sort) {
        PagedModelEntityModelCodedValue pagedModelEntityModelCodedValue = getApi().getCollectionResourceCodedvalueGet1(page, size, sort);
        return Optional.ofNullable(pagedModelEntityModelCodedValue.getEmbedded()).orElse(new PagedModelEntityModelCodedValueEmbedded().codedValues(Collections.emptyList())).getCodedValues();
    }

    @Override
    public void save(EntityModelCodedValue codedValue) {
        CodedValueRequestBody codedValueRequestBody = new CodedValueRequestBody();
        codedValueRequestBody.setCode(codedValue.getCode());
        codedValueRequestBody.setValue(codedValue.getValue());

        if (codedValue.getExternalId() != null && !codedValue.getExternalId().isEmpty()) {
            // update
            codedValueRequestBody.setId(Long.parseLong(codedValue.getExternalId()));
            getApi().putItemResourceCodedvaluePut(codedValue.getExternalId(), codedValueRequestBody);
        } else {
            // create
            getApi().postCollectionResourceCodedvaluePost(codedValueRequestBody);
        }
    }

    @Override
    public void delete(EntityModelCodedValue entityModelCodedValue) {
        getApi().deleteItemResourceCodedvalueDelete(entityModelCodedValue.getExternalId());
    }

}
