package com.myorg.views.forms.security;

import com.myorg.dto.request.security.ProfileRequest;
import com.myorg.dto.response.security.PermitFetchDetail;
import com.myorg.dto.response.security.PermitFetchResponse;
import com.myorg.dto.response.security.PermitRow;
import com.myorg.dto.response.security.ProfileResponse;
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
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Route(value = "form-profile/:id?/:view?", layout = MainLayout.class)
public class FormProfile extends BaseForm<ProfileRequest> {

    private final CovidAnalyticsService service;
    private final SecurityUtils         securityUtils;

    private ProfileResponse responseGet;

    //header
    private TextField       tfName;
    private TextArea        tfDescription;
    private List<PermitRow> details;
    private List<PermitRow> detailsDelete;

    //details
    private Grid<PermitRow>                  grid;
    private CheckboxGroup<PermitFetchDetail> permitChecks;
    private Button btnAdd;
    private FormLayout detailForm;

    public FormProfile(CovidAnalyticsService service, SecurityUtils securityUtils) {
        super(BaseForm.TYPE_TABS_AS_DETAILS);
        this.service = service;
        this.securityUtils = securityUtils;
        details = new ArrayList<>();
        detailsDelete = new ArrayList<>();
    }

    @Override
    protected void setComponentValues() {
        try {
            PermitFetchResponse response = service.getPermits();

            if (response == null) {
                throw new RuntimeException("No response from server on PermitFetchResponse");
            }

            if ((response.data() == null
                    || response.data().permits() == null)
                    && response.responseInfo() != null) {
                throw new RuntimeException(response.responseInfo().message());
            }

            permitChecks.setItems(response.data().permits());
        } catch (Exception e) {
            new ErrorNotification(e.getMessage());
        }
    }

    @Override
    protected void buildComponents() {

        tfName = new TextField("Name");
        tfName.setRequired(true);
        tfName.setRequiredIndicatorVisible(true);
        tfName.setErrorMessage("Complete the required fields");
        tfName.setPlaceholder("Name" + "...");
        tfName.setSizeFull();

        tfDescription = new TextArea("Description");
        tfDescription.setRequired(true);
        tfDescription.setRequiredIndicatorVisible(true);
        tfDescription.setErrorMessage("Complete the required fields");
        tfDescription.setPlaceholder("Description" + "...");
        tfDescription.setSizeFull();
        tfDescription.setMaxRows(2);

        formLayout.add(tfName, 1);
        formLayout.add(tfDescription, 3);

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

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
        hl.setSpacing(true);
        hl.setPadding(false);
        hl.setMargin(false);
        hl.add(buildDetailsForm());
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
            Set<PermitFetchDetail> items = permitChecks.getSelectedItems();
            for (PermitFetchDetail item : items) {
                if(details.stream().noneMatch(it-> it.code().equalsIgnoreCase(item.code()))) {
                    details.add(PermitRow.builder()
                                    .id(item.id())
                                    .permit(item.permit())
                                    .code(item.code())
                            .build());
                }
            }
            grid.setItems(details);
            permitChecks.clear();
        });

        permitChecks = new CheckboxGroup<>("Permits");
        permitChecks.setRequired(true);
        permitChecks.setRequiredIndicatorVisible(true);
        permitChecks.setItemLabelGenerator(PermitFetchDetail::permit);

        detailForm = new FormLayout();
        detailForm.setMinWidth("50%");
        detailForm.setMaxWidth("50%");
        detailForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2),
                new FormLayout.ResponsiveStep("900px", 3),
                new FormLayout.ResponsiveStep("1200px", 3));

        detailForm.add(permitChecks, 1);
        detailForm.add(btnAdd, 1);
        return detailForm;
    }

    private void configGrid() {
        grid = new Grid<>();
        grid.addThemeVariants(
                GridVariant.LUMO_COMPACT,
                GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_WRAP_CELL_CONTENT
        );
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setColumnReorderingAllowed(false);
        grid.addColumn(PermitRow::permit).setHeader("Permit").setWidth("200px")
                .setFlexGrow(1).setResizable(true).setSortable(false);
        grid.addComponentColumn(it -> {
            Button btnDelete = new Button(new Icon(VaadinIcon.TRASH));
            btnDelete.setEnabled(!view);
            btnDelete.setSizeFull();
            btnDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            btnDelete.addClickListener(e -> {

                details.remove(it);
                if(it.profilePermitId() != null && it.profilePermitId() != 0L) {
                    detailsDelete.add(it);
                }
                grid.setItems(details);

                new SuccessNotification("Successfully deleted the permit");
            });

            return btnDelete;
        }).setHeader("").setKey("action").setResizable(true).setFlexGrow(0).setWidth("75px");
    }

    @Override
    protected void enableVisualizationOnly() {
        tfName.setReadOnly(true);
        tfDescription.setReadOnly(true);
        btnSave.setEnabled(false);

        detailForm.setVisible(false);
        permitChecks.setEnabled(false);
        btnAdd.setVisible(false);
    }

    @Override
    protected void fillFields() {
        try {

            if (responseGet == null) {
                throw new RuntimeException("No response from server on responseGet");
            }

            if (responseGet.data() == null && responseGet.responseInfo() != null) {
                throw new RuntimeException(responseGet.responseInfo().message());
            }

            tfName.setValue(responseGet.data().name());
            tfDescription.setValue(responseGet.data().description());

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

        if ((tfDescription.isRequired() || tfDescription.isRequiredIndicatorVisible())
                && (StringUtils.isBlank(tfDescription.getValue())
                || tfDescription.isInvalid())) {
            isError = true;
            tfDescription.setInvalid(true);
        } else {
            tfDescription.setInvalid(false);
        }

        if ((tfName.isRequired() || tfName.isRequiredIndicatorVisible()) && (
                StringUtils.isBlank(tfName.getValue()) || tfName.isInvalid())) {
            isError = true;
            tfName.setInvalid(true);
        } else {
            tfName.setInvalid(false);
        }

        return isError;
    }

    @Override
    protected void save() {

        if (verifyFields()) {
            return;
        }

        try {

            ProfileResponse response = service.postProfile(ProfileRequest.builder()
                    .id(responseGet != null ? responseGet.data().id() : null)
                    .name(tfName.getValue()).description(tfDescription.getValue().trim())
                            .permits(details).permitsDelete(detailsDelete)
                    .build());

            if (response == null) {
                throw new RuntimeException("No response from server on postProfile");
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
            title = "Create" + " " + "Profile";
        } else if (objectToSave.id() != 0L && !view) {
            title = "Edit" + " " + "Profile" + " - ID [" + objectToSave.id()
                    + "]";
        } else if (objectToSave.id() != 0L && view) {
            title = "View" + " " + "Profile" + " - ID [" + objectToSave.id()
                    + "]";
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

        responseGet = service.getProfile(headerId);
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
            if (!securityUtils.isAccessGranted(PermitConstants.PROFILE_VIEW)) {
                event.getUI().getPage().getHistory().back();
            }
        } else if (!securityUtils.isAccessGranted(PermitConstants.PROFILE_EDIT)) {
            event.getUI().getPage().getHistory().back();
        }
    }

    private void buildObjectToSave(ProfileResponse response) {
        try {
            if (response == null) {
                throw new RuntimeException("No response from server on buildObjectToSave");
            }

            if (response.data() == null && response.responseInfo() != null) {
                throw new RuntimeException(response.responseInfo().message());
            }

            objectToSave = ProfileRequest.builder().id(response.data().id())
                    .name(response.data().name())
                    .description(response.data().description())
                    .permits(response.data().permits())
                    .build();

            details = objectToSave.permits();
            grid.setItems(details);
        } catch (Exception e) {
            new ErrorNotification(e.getMessage());
        }
    }
}
