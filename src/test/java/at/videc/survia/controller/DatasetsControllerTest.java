package at.videc.survia.controller;

import at.videc.survia.restclient.api.DatasetEntityControllerApi;
import at.videc.survia.restclient.model.*;
import at.videc.survia.ui.controller.DatasetsController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DatasetsControllerTest {

    @InjectMocks
    private DatasetsController datasetsController;

    @Mock
    private DatasetEntityControllerApi datasetEntityControllerApi;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCount() {
        PagedModelEntityModelDataset pagedModel = new PagedModelEntityModelDataset().page(new PageMetadata().totalElements(10L));

        when(datasetEntityControllerApi.getCollectionResourceDatasetGet1(anyInt(), anyInt(), any())).thenReturn(pagedModel);

        int count = datasetsController.count(1, 10);
        assertEquals(10, count);
    }

    @Test
    public void testList() {
        EntityModelDataset dataset = new EntityModelDataset();
        PagedModelEntityModelDataset pagedModel = new PagedModelEntityModelDataset().embedded(new PagedModelEntityModelDatasetEmbedded().datasets(Collections.singletonList(dataset)));

        when(datasetEntityControllerApi.getCollectionResourceDatasetGet1(anyInt(), anyInt(), any())).thenReturn(pagedModel);

        var result = datasetsController.list(1, 10, Collections.emptyList());
        assertEquals(1, result.size());
        assertEquals(dataset, result.get(0));
    }

    @Test
    public void testSave() {
        EntityModelDataset dataset = new EntityModelDataset();
        dataset.setExternalId("1");
        dataset.setName("Test");
        dataset.setDescription("Test Description");
        dataset.setOrganization("Test Organization");

        when(datasetEntityControllerApi.putItemResourceDatasetPut(anyString(), any(DatasetRequestBody.class))).thenReturn(dataset);

        datasetsController.save(dataset);

        verify(datasetEntityControllerApi, times(1)).putItemResourceDatasetPut(anyString(), any(DatasetRequestBody.class));
    }

    @Test
    public void testDelete() {
        EntityModelDataset dataset = new EntityModelDataset();
        dataset.setExternalId("1");

        doNothing().when(datasetEntityControllerApi).deleteItemResourceDatasetDelete(anyString());

        datasetsController.delete(dataset);

        verify(datasetEntityControllerApi, times(1)).deleteItemResourceDatasetDelete(anyString());
    }
}