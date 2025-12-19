package com.myorg.views.generics;

import com.myorg.config.security.MyVaadinSession;
import com.myorg.encapsulations.UserSetting;
import com.myorg.utils.Utilities;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinSession;

import java.util.Date;

public class SecurityForm extends VerticalLayout {

    private TextField tfId, tfCreatedBy, tfModifiedBy, tfCreationDate, tfModifiedDate,
            tfVersion;
    private   Checkbox    checkEnabled;
    protected UserSetting userSetting;

    public SecurityForm() {
        this.userSetting = (UserSetting) VaadinSession.getCurrent()
                .getAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString());

        tfId = new TextField(UI.getCurrent().getTranslation("databaseID"));
        tfId.setReadOnly(true);

        tfCreatedBy = new TextField(UI.getCurrent().getTranslation("createdby"));
        tfCreatedBy.setReadOnly(true);

        tfModifiedBy = new TextField(UI.getCurrent().getTranslation("modifiedby"));
        tfModifiedBy.setReadOnly(true);

        tfCreationDate = new TextField(UI.getCurrent().getTranslation("creationdate"));
        tfCreationDate.setReadOnly(true);

        tfModifiedDate =
                new TextField(UI.getCurrent().getTranslation("lastmodificationdate"));
        tfModifiedDate.setReadOnly(true);

        tfVersion = new TextField(UI.getCurrent().getTranslation("version"));
        tfVersion.setReadOnly(true);

        checkEnabled = new Checkbox(UI.getCurrent().getTranslation("enabled"));
        checkEnabled.setReadOnly(true);

        FormLayout formLayout = new FormLayout();
        formLayout.setSizeFull();
        formLayout.add(tfId);
        formLayout.add(tfCreatedBy);
        formLayout.add(tfModifiedBy);
        formLayout.add(tfCreationDate);
        formLayout.add(tfModifiedDate);
        formLayout.add(checkEnabled);
        formLayout.add(tfVersion);

        setMargin(false);
        setPadding(false);
        setSpacing(true);
        addAndExpand(formLayout);
    }

    public SecurityForm(Object object) {
        this();
        fillFields(object);
    }

    public void fillFields(Object object) {
        if (object == null) {
            return;
        }

        try {
            setFieldId(Utilities.getFieldValue(object, "id").toString());
        } catch (Exception e) {
            setFieldId("-1");
        }

        try {
            setCreatedBy(Utilities.getFieldValue(object, "createdBy").toString());
        } catch (Exception e) {
            setCreatedBy("-1");
        }

        try {
            setModifiedBy(Utilities.getFieldValue(object, "modifiedBy").toString());
        } catch (Exception e) {
            setModifiedBy("-1");
        }

        try {
            setCreationDate((String) Utilities.getFieldValue(object, "dateCreated"));
        } catch (Exception e) {
            setCreationDate("-1");
        }

        try {
            setLastModificationDate(
                    (String) Utilities.getFieldValue(object, "lastUpdated"));
        } catch (Exception e) {
            setLastModificationDate("-1");
        }

        try {
            setEnabledField((Boolean) Utilities.getFieldValue(object, "enabled"));
        } catch (Exception e) {
            setEnabledField(false);
        }

        try {
            setVersion(Utilities.getFieldValue(object, "version").toString());
        } catch (Exception e) {
            setVersion("-1");
        }
    }

    private void setFieldId(String value) {
        tfId.setValue(value);
    }

    private void setCreatedBy(String value) {
        tfCreatedBy.setValue(value);
    }

    private void setModifiedBy(String value) {
        tfModifiedBy.setValue(value);
    }

    private void setCreationDate(String value) {
        tfCreationDate.setValue(value);
    }

    private void setLastModificationDate(String value) {
        tfModifiedDate.setValue(value);
    }

    private void setEnabledField(boolean value) {
        checkEnabled.setValue(value);
    }

    private void setVersion(String value) {
        tfVersion.setValue(value);
    }

    public String getIdFieldValue() {
        return tfId.getValue();
    }
}
