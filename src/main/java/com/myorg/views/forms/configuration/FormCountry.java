package com.myorg.views.forms.configuration;

import com.myorg.dto.request.configurations.CountryRequest;
import com.myorg.dto.response.configuration.CountryResponse;
import com.myorg.dto.response.security.PermitFetchDetail;
import com.myorg.dto.response.security.PermitRow;
import com.myorg.service.CovidAnalyticsService;
import com.myorg.utils.PermitConstants;
import com.myorg.utils.SecurityUtils;
import com.myorg.views.MainLayout;
import com.myorg.views.generics.BaseForm;
import com.myorg.views.generics.notifications.ErrorNotification;
import com.myorg.views.generics.notifications.SuccessNotification;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@Route(value = "form-country/:id?/:view?", layout = MainLayout.class)
public class FormCountry extends BaseForm<CountryRequest> {

    private final CovidAnalyticsService service;
    private final SecurityUtils         securityUtils;

    private CountryResponse responseGet;

    //header
    private TextField       tfName, tfCode, tfWikiId, tfDataCommons, tfIso2, tfIso3, tfPlaceId;

    //details
    private Grid<PermitRow>                        grid;
    private CheckboxGroup<PermitFetchDetail>       permitChecks;
    private MultiSelectComboBox<PermitFetchDetail> permitsCombo;
    private Button                                 btnAdd;
    private FormLayout                             detailForm;

    public FormCountry(CovidAnalyticsService service, SecurityUtils securityUtils) {
        super(BaseForm.TYPE_TABS_AS_DETAILS);
        this.service = service;
        this.securityUtils = securityUtils;
    }

    @Override
    protected void setComponentValues() {
    }

    @Override
    protected void buildComponents() {

        tfName = new TextField("Name");
        tfName.setRequired(true);
        tfName.setRequiredIndicatorVisible(true);
        tfName.setErrorMessage("Complete the required fields");
        tfName.setPlaceholder("Name" + "...");
        tfName.setSizeFull();

        tfCode = new TextField("Country Code");
        tfCode.setRequired(true);
        tfCode.setRequiredIndicatorVisible(true);
        tfCode.setErrorMessage("Complete the required fields");
        tfCode.setPlaceholder("Country Code" + "...");
        tfCode.setSizeFull();

        tfWikiId = new TextField("Wiki Data ID");
        tfWikiId.setRequired(true);
        tfWikiId.setRequiredIndicatorVisible(true);
        tfWikiId.setErrorMessage("Complete the required fields");
        tfWikiId.setPlaceholder("Wiki Data ID" + "...");
        tfWikiId.setSizeFull();

        tfPlaceId = new TextField("Place ID");
        tfPlaceId.setRequired(true);
        tfPlaceId.setRequiredIndicatorVisible(true);
        tfPlaceId.setErrorMessage("Complete the required fields");
        tfPlaceId.setPlaceholder("Place ID" + "...");
        tfPlaceId.setSizeFull();

        tfDataCommons = new TextField("Data Commons ID");
        tfDataCommons.setRequired(true);
        tfDataCommons.setRequiredIndicatorVisible(true);
        tfDataCommons.setErrorMessage("Complete the required fields");
        tfDataCommons.setPlaceholder("Data Commons ID" + "...");
        tfDataCommons.setSizeFull();

        tfIso3 = new TextField("ISO 3166 Alpha 2");
        tfIso3.setRequired(true);
        tfIso3.setRequiredIndicatorVisible(true);
        tfIso3.setErrorMessage("Complete the required fields");
        tfIso3.setPlaceholder("ISO 3166 Alpha 2" + "...");
        tfIso3.setSizeFull();

        tfIso2 = new TextField("ISO 3166 Alpha 3");
        tfIso2.setRequired(true);
        tfIso2.setRequiredIndicatorVisible(true);
        tfIso2.setErrorMessage("Complete the required fields");
        tfIso2.setPlaceholder("ISO 3166 Alpha 3" + "...");
        tfIso2.setSizeFull();

        formLayout.add(tfName, tfCode, tfDataCommons, tfPlaceId, tfWikiId, tfIso2, tfIso3);

    }

    @Override
    protected void enableVisualizationOnly() {
        tfName.setReadOnly(true);
        tfCode.setReadOnly(true);
        tfPlaceId.setReadOnly(true);
        tfWikiId.setReadOnly(true);
        tfDataCommons.setReadOnly(true);
        tfIso2.setReadOnly(true);
        tfIso3.setReadOnly(true);

        btnSave.setEnabled(false);
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
            tfCode.setValue(responseGet.data().code());
            tfDataCommons.setValue(responseGet.data().dataCommonsId());
            tfPlaceId.setValue(responseGet.data().placeId());
            tfWikiId.setValue(responseGet.data().wikiDataId());
            tfIso2.setValue(responseGet.data().iso_3166_1_alpha_2());
            tfIso3.setValue(responseGet.data().iso_3166_1_alpha_3());

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

        if ((tfName.isRequired() || tfName.isRequiredIndicatorVisible()) && (
                StringUtils.isBlank(tfName.getValue()) || tfName.isInvalid())) {
            isError = true;
            tfName.setInvalid(true);
        } else {
            tfName.setInvalid(false);
        }

        if ((tfCode.isRequired() || tfCode.isRequiredIndicatorVisible()) && (
                StringUtils.isBlank(tfCode.getValue()) || tfCode.isInvalid())) {
            isError = true;
            tfCode.setInvalid(true);
        } else {
            tfCode.setInvalid(false);
        }

        if ((tfPlaceId.isRequired() || tfPlaceId.isRequiredIndicatorVisible()) && (
                StringUtils.isBlank(tfPlaceId.getValue()) || tfPlaceId.isInvalid())) {
            isError = true;
            tfPlaceId.setInvalid(true);
        } else {
            tfPlaceId.setInvalid(false);
        }

        if ((tfWikiId.isRequired() || tfWikiId.isRequiredIndicatorVisible()) && (
                StringUtils.isBlank(tfWikiId.getValue()) || tfWikiId.isInvalid())) {
            isError = true;
            tfWikiId.setInvalid(true);
        } else {
            tfWikiId.setInvalid(false);
        }

        if ((tfIso3.isRequired() || tfIso3.isRequiredIndicatorVisible()) && (
                StringUtils.isBlank(tfIso3.getValue()) || tfIso3.isInvalid())) {
            isError = true;
            tfIso3.setInvalid(true);
        } else {
            tfIso3.setInvalid(false);
        }

        if ((tfIso2.isRequired() || tfIso2.isRequiredIndicatorVisible()) && (
                StringUtils.isBlank(tfIso2.getValue()) || tfIso2.isInvalid())) {
            isError = true;
            tfIso2.setInvalid(true);
        } else {
            tfIso2.setInvalid(false);
        }

        if ((tfDataCommons.isRequired() || tfDataCommons.isRequiredIndicatorVisible()) && (
                StringUtils.isBlank(tfDataCommons.getValue()) || tfDataCommons.isInvalid())) {
            isError = true;
            tfDataCommons.setInvalid(true);
        } else {
            tfDataCommons.setInvalid(false);
        }

        return isError;
    }

    @Override
    protected void save() {

        if (verifyFields()) {
            return;
        }

        try {

            CountryResponse response = service.postCountry(CountryRequest.builder()
                    .id(responseGet != null ? responseGet.data().id() : null)
                    .name(tfName.getValue().trim())
                    .code(tfCode.getValue().trim().toUpperCase())
                    .dataCommonsId(tfDataCommons.getValue().trim())
                    .iso_3166_1_alpha_2(tfIso2.getValue().trim())
                    .iso_3166_1_alpha_3(tfIso3.getValue().trim())
                    .placeId(tfPlaceId.getValue().trim())
                    .wikiDataId(tfWikiId.getValue().trim())
                    .build());

            if (response == null) {
                throw new RuntimeException("No response from server on postCountry");
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
            title = "Create" + " " + "Country";
        } else if (objectToSave.id() != 0L && !view) {
            title = "Edit" + " " + "Country" + " - ID [" + objectToSave.id() + "]";
        } else if (objectToSave.id() != 0L && view) {
            title = "View" + " " + "Country" + " - ID [" + objectToSave.id() + "]";
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

        responseGet = service.getCountry(headerId);
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

    private void buildObjectToSave(CountryResponse response) {
        try {
            if (response == null) {
                throw new RuntimeException(
                        "No response from server on buildObjectToSave");
            }

            if (response.data() == null && response.responseInfo() != null) {
                throw new RuntimeException(response.responseInfo().message());
            }

            objectToSave = CountryRequest.builder()
                    .id(response.data().id())
                    .name(response.data().name())
                    .code(response.data().code())
                    .wikiDataId(response.data().wikiDataId())
                    .placeId(response.data().placeId())
                    .dataCommonsId(response.data().dataCommonsId())
                    .iso_3166_1_alpha_2(response.data().iso_3166_1_alpha_2())
                    .iso_3166_1_alpha_3(response.data().iso_3166_1_alpha_3())
                    .build();

        } catch (Exception e) {
            new ErrorNotification(e.getMessage());
        }
    }
}
