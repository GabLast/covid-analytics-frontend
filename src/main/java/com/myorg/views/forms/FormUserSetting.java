package com.myorg.views.forms;

import com.myorg.config.security.MyVaadinSession;
import com.myorg.dto.request.configurations.UserSettingRequest;
import com.myorg.encapsulations.User;
import com.myorg.service.CovidAnalyticsService;
import com.myorg.views.generics.dialog.ConfirmWindow;
import com.myorg.views.generics.notifications.ErrorNotification;
import com.myorg.views.generics.notifications.SuccessNotification;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ModalityMode;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class FormUserSetting extends Dialog {

    private CovidAnalyticsService service;

    private Button btnExit, btnSave;
    private FormLayout formLayout;

    private ComboBox<Locale> cbLocale;
    private ComboBox<String> cbTimeZone;
    private ComboBox<String> cbDateFormat;
    private ComboBox<String> cbDateTimeFormat;
    private Checkbox checkDarkMode;

    protected Callback callback;
    protected User user;
    protected UserSettingRequest objectToSave;

    public interface Callback {
        void run(UserSettingRequest userSetting);
    }

    public FormUserSetting(CovidAnalyticsService service, Callback callback) {
        this.service = service;
        this.user = (User) VaadinSession.getCurrent().getAttribute(MyVaadinSession.SessionVariables.USER.toString());
        this.objectToSave = (UserSettingRequest) VaadinSession.getCurrent().getAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString());
        this.callback = callback;

        setMinWidth("30%");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);
        setModality(ModalityMode.VISUAL);

        setHeaderTitle("Settings");
        add(buildForm());
        buildBtns();
        buildComponents();
        setComponentValues();
        fillFields();
        open();
    }

    private void setComponentValues() {
        cbTimeZone.setItems(TimeZone.getAvailableIDs());

        cbDateFormat.setItems(List.of(
                "dd/MM/yy",
                "MM/dd/yy",
                "d/M/yy",
                "M/d/yy"
        ));

        cbDateTimeFormat.setItems(List.of(
                "dd/MM/yy hh:mm a",
                "dd/MM/yy hh:mm",
                "MM/dd/yy hh:mm a",
                "MM/dd/yy hh:mm",
                "d/M/yy hh:mm a",
                "d/M/yy hh:mm",
                "M/d/yy hh:mm a",
                "M/d/yy hh:mm"
        ));
    }

    private Component buildSeparation() {
        Hr hr = new Hr();
        hr.getStyle().set("padding", "0").set("margin", "0");
        return hr;
    }

    protected void buildBtns() {
        btnExit = new Button("Exit", new Icon(VaadinIcon.ARROW_LEFT));
        btnExit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnExit.addClickListener(e -> {
            close();
        });

        btnSave = new Button("Save", new Icon(VaadinIcon.CHECK));
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        btnSave.addClickListener(buttonClickEvent -> {
            ConfirmWindow confirmWindow =
                    new ConfirmWindow(this::save);
            confirmWindow.open();
        });

        getFooter().add(btnExit);
        getFooter().add(btnSave);
    }

    private Component buildForm() {

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(false);
        layout.setMargin(false);

        formLayout = new FormLayout();
        formLayout.setSizeUndefined();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2),
                new FormLayout.ResponsiveStep("900px", 3),
                new FormLayout.ResponsiveStep("1200px", 4));

        layout.add(formLayout);

        return layout;
    }

    private void buildComponents() {

        cbDateFormat = new ComboBox<>();
        cbDateFormat.setLabel("Date Format");
        cbDateFormat.setRequiredIndicatorVisible(true);
        cbDateFormat.setErrorMessage("Fill the required fields");
        cbDateFormat.setSizeFull();

        cbDateTimeFormat = new ComboBox<>();
        cbDateTimeFormat.setLabel("Date-Time Format");
        cbDateTimeFormat.setRequiredIndicatorVisible(true);
        cbDateTimeFormat.setErrorMessage("Fill the required fields");
        cbDateTimeFormat.setSizeFull();

        cbTimeZone = new ComboBox<>();
        cbTimeZone.setLabel("Timezone");
        cbTimeZone.setRequiredIndicatorVisible(true);
        cbTimeZone.setErrorMessage("Fill the required fields");
        cbTimeZone.setSizeFull();

        checkDarkMode = new Checkbox("Dark Mode");

        formLayout.add(cbTimeZone);
        formLayout.add(cbDateFormat, cbDateTimeFormat);
        formLayout.add(checkDarkMode);
    }

    private void fillFields() {
        if (objectToSave != null) {
//            cbDateFormat.setValue(objectToSave.getDateFormat());
//            cbDateTimeFormat.setValue(objectToSave.getDateTimeFormat());
//            cbTimeZone.setValue(objectToSave.getTimeZoneString());
//            checkDarkMode.setValue(objectToSave.isDarkMode());
//            cbLocale.setValue(objectToSave.getLocale());
        }
    }

    private boolean verifyFields() {
        boolean isError = false;

        if (cbLocale.isRequiredIndicatorVisible() && (cbLocale.getValue() == null || cbLocale.isInvalid())) {
            isError = true;
            cbLocale.setInvalid(true);
        } else {
            cbLocale.setInvalid(false);
        }

        if (cbTimeZone.isRequiredIndicatorVisible() && (cbTimeZone.getValue() == null || cbTimeZone.isInvalid())) {
            isError = true;
            cbTimeZone.setInvalid(true);
        } else {
            cbTimeZone.setInvalid(false);
        }

        if (cbDateFormat.isRequiredIndicatorVisible() && (cbDateFormat.getValue() == null || cbDateFormat.isInvalid())) {
            isError = true;
            cbDateFormat.setInvalid(true);
        } else {
            cbDateFormat.setInvalid(false);
        }

        if (cbDateTimeFormat.isRequiredIndicatorVisible() && (cbDateTimeFormat.getValue() == null || cbDateTimeFormat.isInvalid())) {
            isError = true;
            cbDateTimeFormat.setInvalid(true);
        } else {
            cbDateTimeFormat.setInvalid(false);
        }

        if (checkDarkMode.getValue() == null) {
            isError = true;
            cbDateTimeFormat.setInvalid(true);
        } else {
            cbDateTimeFormat.setInvalid(false);
        }

        return isError;
    }

    private void save() {

        try {

            if (verifyFields()) {
                return;
            }

//            objectToSave.setUser(user);
//            objectToSave.setTimeZoneString(cbTimeZone.getValue());
//            objectToSave.setDateFormat(cbDateFormat.getValue());
//            objectToSave.setDateTimeFormat(cbDateTimeFormat.getValue());
//            objectToSave.setDarkMode(checkDarkMode.getValue());
//            objectToSave.setLanguage(cbLocale.getValue().getLanguage());
//            objectToSave = userSettingService.saveAndFlush(objectToSave);

            new SuccessNotification(UI.getCurrent().getTranslation("action.save"));

            callback.run(objectToSave);

            close();
        } catch (Exception e) {
            new ErrorNotification(e.getMessage());
        }
    }

}
