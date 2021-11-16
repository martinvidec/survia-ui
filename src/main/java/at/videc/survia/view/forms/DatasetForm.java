package at.videc.survia.view.forms;

import at.videc.survia.controller.ErrorHandlingController;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import org.openapitools.client.model.EntityModelDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import sun.misc.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DatasetForm extends FormLayout {

    private final static Logger LOG = LoggerFactory.getLogger(DatasetForm.class);

    private TextField nameField;
    private TextField descriptionField;
    private TextField organizationField;
    private Upload logoUpload;
    private Image logoImage;
    private TextField externalIdField;

    private Button saveBtn;
    private Button cancelBtn;

    private HorizontalLayout btnLayout;

    private Binder<EntityModelDataset> binder;

    private MemoryBuffer memoryBuffer;

    public DatasetForm() {
        initComponents();
        initLayout();
    }

    public void setDataset(EntityModelDataset entityModelDataset) {
        binder.readBean(entityModelDataset);
        // TODO logo image

        // if logo is set...
        // logo is a List<Byte[]> why?
        if (entityModelDataset.getLogo() == null ||
                entityModelDataset.getLogo().isEmpty() ||
                entityModelDataset.getLogo().get(0) == null ||
                entityModelDataset.getLogo().get(0).length == 0
        ) {
            return;
        }

        // logo is a List<Byte[]> why?
        int length = 0;
        for (byte[] bytes : entityModelDataset.getLogo()) {
            length += bytes.length;
        }

        byte[] logo = new byte[length];
        int i = 0;
        for (byte[] bytes : entityModelDataset.getLogo()) {
            for (byte b : bytes) {
                logo[i] = b;
                i++;
            }
        }

        logoImage.setSrc(new String(logo, StandardCharsets.UTF_8));
        logoImage.setVisible(true);
    }

    public EntityModelDataset getDataset() throws ValidationException {
        EntityModelDataset dataset = new EntityModelDataset();
        binder.writeBean(dataset);
        // TODO handle Upload
        if(logoImage.isVisible()) {
            List<byte[]> logo = new ArrayList<>();
            logo.add(logoImage.getSrc().getBytes(StandardCharsets.UTF_8));
            dataset.setLogo(logo);
        }
        return dataset;
    }

    private void initComponents() {
        externalIdField = new TextField();
        externalIdField.setLabel("External Id");
        externalIdField.setReadOnly(true);

        nameField = new TextField();
        nameField.setLabel("Name");
        nameField.setPlaceholder("Name of the Dataset");

        descriptionField = new TextField();
        descriptionField.setLabel("Description");
        descriptionField.setPlaceholder("Description of the dataset");

        organizationField = new TextField();
        organizationField.setLabel("Organization");
        organizationField.setPlaceholder("Name of your organization");

        logoImage = new Image();
        logoImage.setVisible(false);

        memoryBuffer = new MemoryBuffer();

        logoUpload = new Upload(memoryBuffer);
        logoUpload.setDropLabel(new Label("drop logo here"));
        logoUpload.setMaxFiles(1);
        logoUpload.addFinishedListener(event -> {
            InputStream inputStream = memoryBuffer.getInputStream();
            try {
                final byte[] logo = StreamUtils.copyToByteArray(inputStream);
                String src = "data:" + memoryBuffer.getFileData().getMimeType() + ";base64, " + Base64.getEncoder().encodeToString(logo);

                getUI().ifPresent(ui -> {
                    logoImage.setSrc(src);
                    logoImage.setVisible(true);
                });
            } catch (IOException e) {
                // TODO Logging
                LOG.error("Can't copy InputStream to byte[]", e);
            }
        });

        saveBtn = new Button("Save");
        cancelBtn = new Button("Cancel");

        binder = new Binder<>(EntityModelDataset.class);
        binder.bind(externalIdField, EntityModelDataset::getExternalId, EntityModelDataset::setExternalId);
        binder.bind(nameField, EntityModelDataset::getName, EntityModelDataset::setName);
        binder.bind(descriptionField, EntityModelDataset::getDescription, EntityModelDataset::setDescription);
        binder.bind(organizationField, EntityModelDataset::getOrganization, EntityModelDataset::setOrganization);

    }

    private void initLayout() {
        btnLayout = new HorizontalLayout();
        btnLayout.add(saveBtn, cancelBtn);

        // TODO use Binder with Validations
        add(nameField, descriptionField, organizationField, logoImage, logoUpload);
    }

    public void addSaveListener(ComponentEventListener<ClickEvent<Button>> listener) {
        saveBtn.addClickListener(listener);
    }

    public void addCancelListener(ComponentEventListener<ClickEvent<Button>> listener) {
        cancelBtn.addClickListener(listener);
    }

    public TextField getNameField() {
        return nameField;
    }

    public void setNameField(TextField nameField) {
        this.nameField = nameField;
    }

    public TextField getDescriptionField() {
        return descriptionField;
    }

    public void setDescriptionField(TextField descriptionField) {
        this.descriptionField = descriptionField;
    }

    public TextField getOrganizationField() {
        return organizationField;
    }

    public void setOrganizationField(TextField organizationField) {
        this.organizationField = organizationField;
    }

    public Button getSaveBtn() {
        return saveBtn;
    }

    public void setSaveBtn(Button saveBtn) {
        this.saveBtn = saveBtn;
    }

    public Button getCancelBtn() {
        return cancelBtn;
    }

    public void setCancelBtn(Button cancelBtn) {
        this.cancelBtn = cancelBtn;
    }

    public HorizontalLayout getBtnLayout() {
        return btnLayout;
    }

    public void setBtnLayout(HorizontalLayout btnLayout) {
        this.btnLayout = btnLayout;
    }
}
