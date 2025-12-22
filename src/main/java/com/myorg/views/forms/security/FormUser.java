package com.myorg.views.forms.security;

import com.myorg.dto.request.security.UserRequest;
import com.myorg.dto.response.security.ProfileFetchDataDetails;
import com.myorg.dto.response.security.ProfileFetchResponse;
import com.myorg.dto.response.security.ProfileRow;
import com.myorg.dto.response.security.UserResponse;
import com.myorg.service.CovidAnalyticsService;
import com.myorg.utils.PermitConstants;
import com.myorg.utils.SecurityUtils;
import com.myorg.views.MainLayout;
import com.myorg.views.generics.BaseForm;
import com.myorg.views.generics.notifications.ErrorNotification;
import com.myorg.views.generics.notifications.SuccessNotification;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Route(value = "form-user/:id?/:view?", layout = MainLayout.class)
public class FormUser extends BaseForm<UserRequest> {

    private final CovidAnalyticsService service;
    private final SecurityUtils         securityUtils;

    private UserResponse responseGet;

    //header
    private TextField tfUsername, tfName;
    private PasswordField    tfPassword;
    private EmailField       tfEmail;
    private Checkbox         checkAdmin;
    private List<ProfileRow> details;
    private List<ProfileRow> detailsDelete;

    //details
    private Grid<ProfileRow>                             grid;
    private CheckboxGroup<ProfileFetchDataDetails>       profilesCheck;
    private MultiSelectComboBox<ProfileFetchDataDetails> profilesCombo;
    private Button                                       btnAdd;
    private FormLayout                                   detailForm;

    public FormUser(CovidAnalyticsService service, SecurityUtils securityUtils) {
        super(BaseForm.TYPE_TABS_AS_DETAILS);
        this.service = service;
        this.securityUtils = securityUtils;
        details = new ArrayList<>();
        detailsDelete = new ArrayList<>();
    }

    @Override
    protected void setComponentValues() {
        try {
            ProfileFetchResponse response = service.getProfiles();

            if (response == null) {
                throw new RuntimeException(
                        "No response from server for ProfileFetchResponse");
            }

            if ((response.data() == null || response.data().profiles() == null)
                    && response.responseInfo() != null) {
                throw new RuntimeException(response.responseInfo().message());
            }

            //            profilesCheck.setItems(response.data().profiles());
            profilesCombo.setItems(response.data().profiles());
        } catch (Exception e) {
            new ErrorNotification(e.getMessage());
        }
    }

    @Override
    protected void buildComponents() {

        tfUsername = new TextField("Username");
        tfUsername.setRequired(true);
        tfUsername.setRequiredIndicatorVisible(true);
        tfUsername.setErrorMessage("Complete the required fields");
        tfUsername.setPlaceholder("Username" + "...");
        tfUsername.setSizeFull();
        tfUsername.setValueChangeMode(ValueChangeMode.TIMEOUT);
        tfUsername.addValueChangeListener(event -> {
            if (tfUsername.isInvalid() || StringUtils.isBlank(event.getValue())) {
                return;
            }

            try {
                UserResponse response = service.getUserByUsernameOrMail(event.getValue());

                if (response == null) {
                    throw new RuntimeException(
                            "No response from server on username validation");
                }

                if (response.data() != null && response.responseInfo() == null
                        && objectToSave == null) {
                    tfUsername.setErrorMessage(
                            "Value already in use. Please select a new one.");
                    tfUsername.setInvalid(true);
                } else {
                    tfUsername.setErrorMessage("Complete the required fields");
                    tfUsername.setInvalid(false);
                }
            } catch (Exception e) {
                new ErrorNotification(e.getMessage());
            }
        });

        tfPassword = new PasswordField("Password");
        tfPassword.setRequired(true);
        tfPassword.setRequiredIndicatorVisible(true);
        tfPassword.setErrorMessage("Complete the required fields");
        tfPassword.setPlaceholder("Password" + "...");
        tfPassword.setSizeFull();

        tfName = new TextField("Name");
        tfName.setRequired(true);
        tfName.setRequiredIndicatorVisible(true);
        tfName.setErrorMessage("Complete the required fields");
        tfName.setPlaceholder("Name" + "...");
        tfName.setSizeFull();

        tfEmail = new EmailField("E-mail");
        tfEmail.setRequired(true);
        tfEmail.setRequiredIndicatorVisible(true);
        tfEmail.setErrorMessage("Complete the required fields");
        tfEmail.setPlaceholder("E-mail" + "...");
        tfEmail.setSizeFull();
        tfEmail.addValueChangeListener(event -> {
            if (tfEmail.isInvalid() || StringUtils.isBlank(event.getValue())) {
                return;
            }

            try {
                UserResponse response = service.getUserByUsernameOrMail(event.getValue());

                if (response == null) {
                    throw new RuntimeException(
                            "No response from server on e-mail validation");
                }

                if (response.data() != null && response.responseInfo() == null
                        && objectToSave == null) {
                    tfEmail.setErrorMessage(
                            "Value already in use. Please select a new one.");
                    tfEmail.setInvalid(true);
                } else {
                    tfEmail.setErrorMessage("Complete the required fields");
                    tfEmail.setInvalid(false);
                }
            } catch (Exception e) {
                new ErrorNotification(e.getMessage());
            }
        });

        checkAdmin = new Checkbox("Administrator");

        formLayout.add(tfUsername, 1);
        formLayout.add(tfPassword, 1);
        formLayout.add(tfEmail, 1);
        formLayout.add(tfName, 1);
        formLayout.add(checkAdmin, 1);

        Component details = buildDetails();

        Tab tabDetails = new Tab("Permits");
        tabs.addTabAsFirst(tabDetails);
        tabs.addSelectedChangeListener(event -> {

            if (event.getSelectedTab() == null) {
                return;
            }

            if (event.getSelectedTab().equals(tabDetails)) {
                contentDiv.add(details);
            }
        });
    }

    private Component buildDetails() {
        configGrid();

        VerticalLayout aux = new VerticalLayout();
        aux.setMargin(false);
        aux.setPadding(false);
        aux.setSpacing(true);
        aux.setMaxWidth("30%");
        aux.add(buildDetailsForm());

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
        hl.setSpacing(true);
        hl.setPadding(false);
        hl.setMargin(false);
        hl.add(aux);
        hl.add(grid);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(false);
        verticalLayout.setPadding(false);
        verticalLayout.setSpacing(true);
        verticalLayout.addAndExpand(hl);

        return verticalLayout;
    }

    private Component buildDetailsForm() {

        btnAdd = new Button("Add");
        btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnAdd.addClickListener(event -> {
            //            Set<ProfileFetchDataDetails> items = profilesCheck.getSelectedItems();
            Set<ProfileFetchDataDetails> items = profilesCombo.getSelectedItems();
            for (ProfileFetchDataDetails item : items) {
                if (details.stream().noneMatch(it -> it.id().equals(item.id()))) {
                    details.add(ProfileRow.builder().id(item.id()).profile(item.name())
                            .id(item.id()).build());
                }
            }
            grid.setItems(details);
            //            profilesCheck.clear();
            profilesCombo.clear();
        });

//        profilesCheck = new CheckboxGroup<>("Profiles");
//        profilesCheck.setRequired(true);
//        profilesCheck.setRequiredIndicatorVisible(true);
//        profilesCheck.setItemLabelGenerator(ProfileFetchDataDetails::name);

        profilesCombo = new MultiSelectComboBox<>();
        profilesCombo.setLabel("Profiles");
        profilesCombo.setRequired(false);
        profilesCombo.setErrorMessage("Fill the required fields");
        profilesCombo.setItemLabelGenerator(ProfileFetchDataDetails::name);

        detailForm = new FormLayout();
        detailForm.setSizeFull();
        detailForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2),
                new FormLayout.ResponsiveStep("900px", 3),
                new FormLayout.ResponsiveStep("1200px", 3));

//        detailForm.add(profilesCheck, 1);
        detailForm.add(profilesCombo, 4);
        detailForm.add(btnAdd, 4);
        return detailForm;
    }

    private void configGrid() {
        grid = new Grid<>();
        grid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setColumnReorderingAllowed(false);
        grid.addComponentColumn(it -> {
                    Button btnDelete = new Button(new Icon(VaadinIcon.TRASH));
                    btnDelete.setEnabled(!view);
                    btnDelete.setSizeFull();
                    btnDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                            ButtonVariant.LUMO_ERROR);
                    btnDelete.addClickListener(e -> {

                        details.remove(it);
                        if (it.profileUserId() != null && it.profileUserId() != 0L) {
                            detailsDelete.add(it);
                        }
                        grid.setItems(details);

                        new SuccessNotification("Successfully deleted the profile");
                    });

                    return btnDelete;
                }).setHeader("").setKey("action").setResizable(true).setFlexGrow(0)
                .setWidth("75px");
        grid.addColumn(ProfileRow::profile).setHeader("Profile").setWidth("200px")
                .setFlexGrow(1).setResizable(true).setSortable(false);
    }

    @Override
    protected void enableVisualizationOnly() {
        tfUsername.setReadOnly(true);
        tfName.setReadOnly(true);
        tfEmail.setReadOnly(true);
        tfPassword.setReadOnly(true);
        checkAdmin.setReadOnly(true);
        btnSave.setEnabled(false);

        detailForm.setVisible(false);
//        profilesCheck.setEnabled(false);
        profilesCombo.setEnabled(false);
        btnAdd.setVisible(false);
    }

    @Override
    protected void fillFields() {
        try {

            if (responseGet == null) {
                throw new RuntimeException("No response from server on response Get");
            }

            if (responseGet.data() == null && responseGet.responseInfo() != null) {
                throw new RuntimeException(responseGet.responseInfo().message());
            }

            tfUsername.setValue(responseGet.data().username());
            tfPassword.setValue(responseGet.data().password());
            tfEmail.setValue(responseGet.data().mail());
            tfName.setValue(responseGet.data().name());
            checkAdmin.setValue(responseGet.data().admin());

            if (view) {
                enableVisualizationOnly();
            }

        } catch (Exception e) {
            new ErrorNotification(e.getMessage());
        }
    }

    @Override
    protected boolean verifyFields() {
        boolean isError = false;

        if ((tfUsername.isRequired() || tfUsername.isRequiredIndicatorVisible()) && (
                StringUtils.isBlank(tfUsername.getValue()) || tfUsername.isInvalid())) {
            isError = true;
            tfUsername.setInvalid(true);
        } else {
            tfUsername.setInvalid(false);
        }

        if ((tfPassword.isRequired() || tfPassword.isRequiredIndicatorVisible()) && (
                StringUtils.isBlank(tfPassword.getValue()) || tfPassword.isInvalid())) {
            isError = true;
            tfPassword.setInvalid(true);
        } else {
            tfPassword.setInvalid(false);
        }

        if ((tfName.isRequired() || tfName.isRequiredIndicatorVisible()) && (
                StringUtils.isBlank(tfName.getValue()) || tfName.isInvalid())) {
            isError = true;
            tfName.setInvalid(true);
        } else {
            tfName.setInvalid(false);
        }

        if ((tfEmail.isRequired() || tfEmail.isRequiredIndicatorVisible()) && (
                StringUtils.isBlank(tfEmail.getValue()) || tfEmail.isInvalid())) {
            isError = true;
            tfEmail.setInvalid(true);
        } else {
            tfEmail.setInvalid(false);
        }

        return isError;
    }

    @Override
    protected void save() {

        if (verifyFields()) {
            return;
        }

        try {

            UserResponse response = service.postUser(UserRequest.builder()
                    .id(responseGet != null ? responseGet.data().id() : null)
                    .username(tfUsername.getValue().trim())
                    .email(tfEmail.getValue().trim()).name(tfName.getValue().trim())
                    .password(tfPassword.getValue()).admin(checkAdmin.getValue())
                    .profiles(details).profilesDelete(detailsDelete).build());

            if (response == null) {
                throw new RuntimeException("No response from server for postUser");
            }

            if (response.data() == null && response.responseInfo() != null) {
                throw new RuntimeException(response.responseInfo().message());
            }

            new SuccessNotification("The data has been successfully saved");

            UI.getCurrent().getPage().getHistory().back();
        } catch (Exception e) {
            new ErrorNotification(e.getMessage());
        }
    }

    @Override
    public String getPageTitle() {
        String title;

        if (objectToSave == null || objectToSave.id() == 0L) {
            title = "Create" + " " + "User";
        } else if (objectToSave.id() != 0L && !view) {
            title = "Edit" + " " + "User" + " - Name [" + objectToSave.name() + "]";
        } else if (objectToSave.id() != 0L && view) {
            title = "View" + " " + "User" + " - Name [" + objectToSave.name() + "]";
        } else {
            title = "No Data";
        }

        return title;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters routeParameters = event.getRouteParameters();

        Optional<String> idParam = routeParameters.get("id");
        if (idParam.isEmpty()) {
            return;
        }

        long headerId;
        try {
            headerId = Long.parseLong(idParam.get());
        } catch (NumberFormatException e) {
            headerId = 0L;
        }

        if (headerId == 0L) {
            return;
        }

        responseGet = service.getUser(headerId);
        if (responseGet == null) {
            event.getUI().getPage().getHistory().back();
            return;
        }

        //check params
        Optional<String> viewParam = routeParameters.get("view");

        buildObjectToSave(responseGet);
        view = viewParam.isPresent() && !StringUtils.isBlank(viewParam.get()) && viewParam
                .get().equalsIgnoreCase("1");

        if (view) {
            if (!securityUtils.isAccessGranted(PermitConstants.USER_VIEW)) {
                event.getUI().getPage().getHistory().back();
            }
        } else if (!securityUtils.isAccessGranted(PermitConstants.USER_EDIT)) {
            event.getUI().getPage().getHistory().back();
        }
    }

    private void buildObjectToSave(UserResponse response) {
        try {
            if (response == null) {
                throw new RuntimeException("No response from server");
            }

            if (response.data() == null && response.responseInfo() != null) {
                throw new RuntimeException(response.responseInfo().message());
            }

            objectToSave = UserRequest.builder().id(response.data().id())
                    .username(response.data().username()).name(response.data().name())
                    .password(response.data().password()).email(response.data().mail())
                    .admin(response.data().admin()).profiles(response.data().profiles())
                    .build();

            details = objectToSave.profiles();
            grid.setItems(details);
        } catch (Exception e) {
            new ErrorNotification(e.getMessage());
        }
    }
}
