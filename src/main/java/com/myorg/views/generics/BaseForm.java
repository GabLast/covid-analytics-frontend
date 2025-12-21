package com.myorg.views.generics;

import com.myorg.config.security.MyVaadinSession;
import com.myorg.encapsulations.User;
import com.myorg.encapsulations.UserSetting;
import com.myorg.utils.Utilities;
import com.myorg.views.generics.dialog.ConfirmWindow;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.server.VaadinSession;

public abstract class BaseForm<T> extends VerticalLayout implements HasDynamicTitle, BeforeEnterObserver, AfterNavigationObserver {

    public static final Boolean TYPE_TABS_ON_TOP = false;
    public static final Boolean TYPE_TABS_AS_DETAILS = true;

    protected Button btnExit, btnSave;
//    protected SecurityForm securityForm;
    protected Tabs tabs;
    protected Tab securityTab, generalTab;
    protected Div contentDiv;
    protected FormLayout formLayout;

    protected User        user;
    protected UserSetting userSetting;
    protected T           objectToSave;
    protected     boolean view;
    private final boolean type;

    protected DatePicker.DatePickerI18n datePickerFormat = new DatePicker.DatePickerI18n();

    public BaseForm(boolean type) {
        this.user = (User) VaadinSession.getCurrent().getAttribute(MyVaadinSession.SessionVariables.USER.toString());
        this.userSetting = (UserSetting) VaadinSession.getCurrent().getAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString());
        this.view = false;
        this.type = type;
        this.datePickerFormat.setDateFormat(userSetting.dateFormat());

        setSizeFull();
        setMargin(false);
        setPadding(false);
        setSpacing(false);

        add(buildBtns());
//        if (!type) {
//            add(buildSeparation());
//        }
        add(buildForm());
        buildComponents();
    }

    protected Component buildBtns() {
        btnExit = new Button("Exit", new Icon(VaadinIcon.ARROW_LEFT));
        btnExit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnExit.addClickListener(e -> {
            UI.getCurrent().getPage().getHistory().back();
        });

        HorizontalLayout hlExit = new HorizontalLayout();
        hlExit.setWidthFull();
        hlExit.setSpacing(true);
        hlExit.setMargin(false);
        hlExit.setJustifyContentMode(JustifyContentMode.START);
        hlExit.add(btnExit);

        btnSave = new Button("Save (F10)", new Icon(VaadinIcon.CHECK));
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        btnSave.addClickShortcut(Key.F10);
        btnSave.addClickListener(buttonClickEvent -> {
            ConfirmWindow confirmWindow =
                    new ConfirmWindow(this::save);
            confirmWindow.open();
        });

        HorizontalLayout hlSave = new HorizontalLayout();
        hlSave.setWidthFull();
        hlSave.setSpacing(true);
        hlSave.setMargin(false);
        hlSave.setJustifyContentMode(JustifyContentMode.END);
        hlSave.add(btnSave);

        HorizontalLayout hlBotones = new HorizontalLayout();
        hlBotones.setWidthFull();
        hlBotones.setMargin(false);
        hlBotones.setSpacing(false);
        hlBotones.setPadding(true);
        hlBotones.setPadding(true);
        hlBotones.getElement().getStyle().set("padding-bottom", "0");
        hlBotones.add(hlExit, hlSave);

        return hlBotones;
    }

    private Component buildSeparation() {
        Hr hr = new Hr();
        hr.getStyle().set("padding", "0").set("margin", "0");
        return hr;
    }

    private Component buildForm() {
        generalTab = new Tab("General");
        generalTab.setVisible(true);
//        securityTab = new Tab("Security");
//        securityTab.setVisible(false);

        if (type) {
//            tabs = new Tabs(securityTab);
            tabs = new Tabs();
        } else {
            tabs = new Tabs(generalTab/*, securityTab*/);
        }
        tabs.setWidthFull();
        tabs.setAutoselect(true);

//        securityForm = new SecurityForm();

        contentDiv = new Div();
        contentDiv.setWidthFull();

        formLayout = new FormLayout();
        formLayout.setSizeUndefined();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2),
                new FormLayout.ResponsiveStep("900px", 3),
                new FormLayout.ResponsiveStep("1200px", 4));

        tabs.addSelectedChangeListener(event -> {

            contentDiv.removeAll();

            if (event.getSelectedTab() == null) {
                return;
            }

            if (type) {
//                if (event.getSelectedTab().equals(securityTab)) {
//                    contentDiv.add(securityForm);
//                }
            } else {
                if (event.getSelectedTab().equals(generalTab)) {
                    contentDiv.add(formLayout);
                }
//                else if (event.getSelectedTab().equals(securityTab)) {
//                    contentDiv.add(securityForm);
//                }
            }
        });

        VerticalLayout vl = new VerticalLayout();
        vl.setWidthFull();
        vl.setSpacing(true);
        vl.setPadding(true);
        vl.setMargin(false);
        vl.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        if (type) {
            vl.add(formLayout);
            vl.add(tabs);
        } else {
            vl.add(tabs);
        }
        vl.addAndExpand(contentDiv);

        HorizontalLayout holderLayout = new HorizontalLayout();
        holderLayout.setSizeFull();
        holderLayout.setSpacing(false);
        holderLayout.setMargin(false);
        holderLayout.setPadding(false);
        holderLayout.add(vl);

        return holderLayout;
    }


    protected abstract void setComponentValues();

    protected abstract void buildComponents();

    protected abstract void enableVisualizationOnly();

    protected abstract void fillFields();

    protected abstract boolean verifyFields();

    protected abstract void save();

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        setComponentValues();

        if (tabs.getChildren().findAny().isPresent()) {
            tabs.setSelectedTab(null);
            tabs.setSelectedTab(null);
            tabs.setSelectedTab(tabs.getTabAt(0));
        }

        if (objectToSave == null) {
            return;
        }

        try {
            Long id = ((Long) Utilities.getFieldValue(objectToSave, "id"));

            if (id != 0L) {
                fillFields();
//                securityForm.fillFields(objectToSave);
//                securityTab.setVisible(true);
            }
        } catch (Exception ignored) {
        }
    }

}
