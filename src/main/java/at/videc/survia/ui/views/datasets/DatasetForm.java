package at.videc.survia.ui.views.datasets;

import at.videc.survia.restclient.model.EntityModelDataset;
import at.videc.survia.ui.views.base.BaseEditForm;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class DatasetForm extends BaseEditForm<EntityModelDataset> {

    private final static Logger LOG = LoggerFactory.getLogger(DatasetForm.class);
    public static final String IMAGE_PNG = "image/png";
    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String IMAGE_GIF = "image/gif";

    private TextField nameField;
    private TextField descriptionField;
    private TextField organizationField;
    private Image logoImage;
    private TextField externalIdField;

    private MemoryBuffer memoryBuffer;

    @Override
    protected Binder<EntityModelDataset> createBinder() {
        return new Binder<>(EntityModelDataset.class);
    }

    @Override
    protected void initComponents() {
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

        Upload logoUpload = new Upload(memoryBuffer);
        logoUpload.setAcceptedFileTypes(IMAGE_PNG, IMAGE_JPEG, IMAGE_GIF);
        logoUpload.setDropLabel(new NativeLabel("drop logo here"));
        logoUpload.setMaxFiles(1);
        logoUpload.addFinishedListener(event -> {
            InputStream inputStream = memoryBuffer.getInputStream();
            try {
                final byte[] logo = StreamUtils.copyToByteArray(inputStream);
                String src = "data:" + memoryBuffer.getFileData().getMimeType() + ";base64," + Base64.getEncoder().encodeToString(logo);

                getUI().ifPresent(ui -> {
                    logoImage.setSrc(src);
                    logoImage.setVisible(true);
                });
            } catch (IOException e) {
                LOG.error("Can't copy InputStream to byte[]", e);
            }
        });

        add(nameField, descriptionField, organizationField, logoImage, logoUpload);
    }

    @Override
    protected void addAdditionalConfirmButtons(HorizontalLayout horizontalLayout) {
        // nothing TODO here.
    }

    @Override
    protected void initBinding(Binder<EntityModelDataset> binder) {
        // TODO use Binder with Validations
        binder.bind(externalIdField, EntityModelDataset::getExternalId, EntityModelDataset::setExternalId);
        binder.bind(nameField, EntityModelDataset::getName, EntityModelDataset::setName);
        binder.bind(descriptionField, EntityModelDataset::getDescription, EntityModelDataset::setDescription);
        binder.bind(organizationField, EntityModelDataset::getOrganization, EntityModelDataset::setOrganization);
    }

    @Override
    protected void afterBinderRead(EntityModelDataset entityModelDataset) {
        // if logo is set...
        if (entityModelDataset.getLogo() == null || entityModelDataset.getLogo().length == 0) {
            return;
        }
        logoImage.setSrc(new String(entityModelDataset.getLogo(), StandardCharsets.UTF_8));
        logoImage.setVisible(true);
    }

    @Override
    protected void afterBinderWrite(EntityModelDataset entityModelDataset) {
        // everything here that cannot be used with binder
        if (logoImage.isVisible()) {
            entityModelDataset.setLogo(logoImage.getSrc().getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    protected EntityModelDataset createEntityModel() {
        return new EntityModelDataset();
    }
}
