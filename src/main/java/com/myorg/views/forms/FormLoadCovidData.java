package com.myorg.views.forms;

import com.myorg.dto.request.process.CovidDetailFilterRequest;
import com.myorg.dto.request.process.CovidLoadRequest;
import com.myorg.dto.response.configuration.CountryFindAllDataDetails;
import com.myorg.dto.response.configuration.CountryFindAllResponse;
import com.myorg.dto.response.process.CovidDetailFilterDataDetails;
import com.myorg.dto.response.process.CovidLoadResponse;
import com.myorg.service.CovidAnalyticsService;
import com.myorg.utils.DateUtilities;
import com.myorg.utils.GlobalConstants;
import com.myorg.utils.PermitConstants;
import com.myorg.utils.SecurityUtils;
import com.myorg.utils.Utilities;
import com.myorg.views.MainLayout;
import com.myorg.views.generics.BaseForm;
import com.myorg.views.generics.notifications.ErrorNotification;
import com.myorg.views.generics.notifications.SuccessNotification;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.SelectVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.streams.UploadHandler;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.crudui.crud.LazyCrudListener;
import org.vaadin.crudui.crud.impl.GridCrud;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Route(value = "form-covid-load/:id?/:view?", layout = MainLayout.class)
public class FormLoadCovidData extends BaseForm<CovidLoadRequest> {

    private final CovidAnalyticsService service;
    private final SecurityUtils         securityUtils;

    private CovidLoadResponse responseGet;
    private boolean           loaded = false;

    //header
    private TextArea   tfDescription;
    private TextArea   tfJson;
    private TextArea   tfJsonURL;
    private DatePicker dpDate;
    private Upload     upload;
    private byte[]     file;

    //details
    private Select<CountryFindAllDataDetails> countryFilter;
    private DatePicker                        startFilter, endFilter;

    private GridCrud<CovidDetailFilterDataDetails> gridCrud;

    public FormLoadCovidData(CovidAnalyticsService service, SecurityUtils securityUtils) {
        super(BaseForm.TYPE_TABS_AS_DETAILS);
        this.service = service;
        this.securityUtils = securityUtils;
    }

    @Override
    protected void setComponentValues() {
        try {
            CountryFindAllResponse countryResponse = service.getCountries();

            if (countryResponse == null) {
                throw new RuntimeException("No response from server");
            }

            if ((countryResponse.data() == null
                    || countryResponse.data().dataList() == null)
                    && countryResponse.responseInfo() != null) {
                throw new RuntimeException(countryResponse.responseInfo().message());
            }

            countryFilter.setItems(countryResponse.data().dataList());
        } catch (Exception e) {
            new ErrorNotification(e.getMessage());
        }
    }

    @Override
    protected void buildComponents() {

        tfDescription = new TextArea("Description");
        tfDescription.setRequired(true);
        tfDescription.setRequiredIndicatorVisible(true);
        tfDescription.setErrorMessage("Complete the required fields");
        tfDescription.setPlaceholder("Description" + "...");
        tfDescription.setSizeFull();
        tfDescription.setMaxRows(2);

        tfJson = new TextArea("Json Object");
        tfJson.setRequired(true);
        tfJson.setRequiredIndicatorVisible(true);
        tfJson.setErrorMessage("Complete the required fields");
        tfJson.setPlaceholder("Json Object" + "...");
        tfJson.setSizeFull();
        tfJson.setMaxRows(2);

        tfJsonURL = new TextArea("Json URL");
        tfJsonURL.setRequired(true);
        tfJsonURL.setRequiredIndicatorVisible(true);
        tfJsonURL.setErrorMessage("Complete the required fields");
        tfJsonURL.setPlaceholder("Json URL" + "...");
        tfJsonURL.setSizeFull();
        tfJsonURL.setMaxRows(2);
        tfJsonURL.setValueChangeMode(ValueChangeMode.TIMEOUT);
        tfJsonURL.addValueChangeListener(e -> {

            if (tfJsonURL.isInvalid() || StringUtils.isBlank(e.getValue())) {
                return;
            }

            tfJsonURL.setInvalid(!Utilities.isValidUrl(e.getValue()));

            if (tfJsonURL.isInvalid()) {
                tfJsonURL.setErrorMessage("Please type a valid URL");
            } else {
                tfJsonURL.setErrorMessage("Complete the required fields");
            }
        });

        dpDate = new DatePicker("Date");
        dpDate.setRequiredIndicatorVisible(true);
        dpDate.setErrorMessage("Complete the required fields");
        dpDate.setSizeFull();
        dpDate.setValue(LocalDate.now());
        dpDate.setReadOnly(true);

        UploadHandler inMemoryUploadHandler =
                UploadHandler.inMemory((metadata, bytes) -> {
                    String fileName = metadata.fileName();
                    String mimeType = metadata.contentType();
                    long contentLength = metadata.contentLength();

                    file = bytes;
                });
        upload = new Upload(inMemoryUploadHandler);
        upload.setAcceptedFileTypes("text/csv", ".csv");
        upload.setMaxFiles(1);
        upload.setMaxFileSize(GlobalConstants.MAX_FILE_SIZE_IN_BYTES);
        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();
            new ErrorNotification(errorMessage, 3);
        });

        formLayout.add(tfDescription, 2);
        formLayout.add(dpDate, 1);
        formLayout.add(upload, 1);
        formLayout.add(tfJson, 2);
        formLayout.add(tfJsonURL, 2);

        Component details = buildDetails();

        Tab tabDetails = new Tab("Data");
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

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setPadding(false);
        verticalLayout.setMargin(false);

        verticalLayout.add(buildDetailsForm());
        configGrid();
        verticalLayout.addAndExpand(gridCrud.getGrid());

        return verticalLayout;
    }

    private Component buildDetailsForm() {

        startFilter = new DatePicker("Start");
        startFilter.setWidthFull();
        startFilter.setClearButtonVisible(true);
        startFilter.getElement().setAttribute("theme", "small");
        startFilter.addValueChangeListener(e -> gridCrud.refreshGrid());
        startFilter.addThemeVariants(DatePickerVariant.LUMO_SMALL);

        endFilter = new DatePicker("End");
        endFilter.setWidthFull();
        endFilter.setClearButtonVisible(true);
        endFilter.getElement().setAttribute("theme", "small");
        endFilter.addValueChangeListener(e -> gridCrud.refreshGrid());
        endFilter.addThemeVariants(DatePickerVariant.LUMO_SMALL);

        countryFilter = new Select<>();
        countryFilter.setLabel("Country");
        countryFilter.setEmptySelectionAllowed(true);
        countryFilter.setEmptySelectionCaption("All");
        countryFilter.getElement().setAttribute("theme", "small");
        countryFilter.addValueChangeListener(e -> gridCrud.refreshGrid());
        countryFilter.addThemeVariants(SelectVariant.LUMO_SMALL);

        FormLayout form = new FormLayout();
        form.setSizeUndefined();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("600px", 2),
                new FormLayout.ResponsiveStep("900px", 3),
                new FormLayout.ResponsiveStep("1200px", 4));

        form.add(countryFilter, 1);
        form.add(startFilter, 1);
        form.add(endFilter, 1);

        return form;
    }

    @Override
    protected void enableVisualizationOnly() {
        tfJson.setReadOnly(true);
        tfJsonURL.setReadOnly(true);
        tfDescription.setReadOnly(true);
        dpDate.setReadOnly(true);
        btnSave.setEnabled(false);
    }

    @Override
    protected void fillFields() {
        try {

            if (responseGet == null) {
                throw new RuntimeException("No response from server");
            }

            if (responseGet.data() == null && responseGet.responseInfo() != null) {
                throw new RuntimeException(responseGet.responseInfo().message());
            }

            tfDescription.setValue(responseGet.data().description());
            tfJson.setValue(responseGet.data().jsonString());
            tfJsonURL.setValue(responseGet.data().jsonURL());
            dpDate.setValue(
                    DateUtilities.getLocalDateFromString(responseGet.data().loadDate(),
                            userSetting));

            if (view) {
                enableVisualizationOnly();
            }

            gridCrud.refreshGrid();
            upload.setVisible(false);
            tfJsonURL.setReadOnly(true);
            tfJson.setReadOnly(true);

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

        if (tfJson.getValue() != null && !StringUtils.isBlank(tfJson.getValue())
                && tfJsonURL.getValue() != null && !StringUtils.isBlank(tfJsonURL.getValue())) {
            isError = true;
            new ErrorNotification("Use either a JSON or JSON URL data loading method", 4);
            return isError;
        }

        if (!loaded) {
            if (((tfJsonURL.isRequired() || tfJsonURL.isRequiredIndicatorVisible()) && (
                    StringUtils.isBlank(tfJsonURL.getValue()) || tfJsonURL.isInvalid()))
                    && ((tfJson.isRequired() || tfJson.isRequiredIndicatorVisible()) && (
                    StringUtils.isBlank(tfJson.getValue()) || tfJson.isInvalid()))
                    && file == null) {
                isError = true;
                tfJsonURL.setInvalid(true);
                tfJson.setInvalid(true);
            } else {
                tfJsonURL.setInvalid(false);
                tfJson.setInvalid(false);
            }
        }

        if (dpDate.isRequiredIndicatorVisible() && (dpDate.getValue() == null
                || dpDate.isInvalid())) {
            isError = true;
            dpDate.setInvalid(true);
        } else {
            dpDate.setInvalid(false);
        }

        return isError;
    }

    private void configGrid() {
        gridCrud = new GridCrud<>(CovidDetailFilterDataDetails.class);

        Grid<CovidDetailFilterDataDetails> grid = gridCrud.getGrid();
        grid.removeAllColumns();

        grid.setSizeFull();
        grid.setPageSize(50);
        grid.setMultiSort(false);
        grid.setColumnReorderingAllowed(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);

        grid.addColumn(CovidDetailFilterDataDetails::country).setKey("country")
                .setHeader("Country").setSortable(true).setResizable(true).setFlexGrow(1);
        grid.addColumn(CovidDetailFilterDataDetails::date).setKey("date")
                .setHeader("Date").setSortable(true).setResizable(true).setFlexGrow(1);
        grid.addColumn(it -> Utilities.formatDecimal(it.new_confirmed(), 0))
                .setKey("new_confirmed").setHeader("New Infections").setSortable(true)
                .setResizable(true).setFlexGrow(1);
        grid.addColumn(it -> Utilities.formatDecimal(it.new_deceased(), 0))
                .setKey("new_deceased").setHeader("New Deceased").setSortable(true)
                .setResizable(true).setFlexGrow(1);
        grid.addColumn(it -> Utilities.formatDecimal(it.new_persons_vaccinated(), 0))
                .setKey("new_persons_vaccinated").setHeader("New Vaccinated")
                .setSortable(true).setResizable(true).setFlexGrow(1);
        grid.addColumn(
                        it -> Utilities.formatDecimal(it.new_persons_fully_vaccinated(), 0))
                .setKey("new_persons_fully_vaccinated").setHeader("New Fully Vaccinated")
                .setSortable(true).setResizable(true).setFlexGrow(1);
        grid.addColumn(
                        it -> Utilities.formatDecimal(it.new_vaccine_doses_administered(), 0))
                .setKey("new_vaccine_doses_administered")
                .setHeader("New Vaccine Doses Administered").setSortable(true)
                .setResizable(true).setFlexGrow(1);

        gridCrud.setCrudListener(configDataSource());
        gridCrud.refreshGrid();
    }

    private LazyCrudListener<CovidDetailFilterDataDetails> configDataSource() {
        try {
            return new LazyCrudListener<>() {
                @Override
                public CovidDetailFilterDataDetails add(CovidDetailFilterDataDetails a) {
                    return null;
                }

                @Override
                public CovidDetailFilterDataDetails update(
                        CovidDetailFilterDataDetails a) {
                    return null;
                }

                @Override
                public void delete(CovidDetailFilterDataDetails a) {
                }

                @Override
                public DataProvider<CovidDetailFilterDataDetails, ?> getDataProvider() {
                    CovidDetailFilterRequest request = new CovidDetailFilterRequest();

                    request.setHeaderId(
                            responseGet != null ? responseGet.data().headerId() : null);
                    request.setCountry(
                            countryFilter.getValue() != null ? countryFilter.getValue()
                                    .code() : null);
                    request.setEnabled(true);
                    request.setDateStart(startFilter.getValue());
                    request.setDateEnd(endFilter.getValue());

                    return DataProvider.fromCallbacks(query -> {
                        request.setLimit(query.getLimit());
                        request.setOffset(query.getOffset());

                        Optional<QuerySortOrder> sort =
                                query.getSortOrders().stream().findAny();
                        if (sort.isPresent()) {
                            request.setSortProperty(sort.get().getSorted());
                            request.setSortOrder(
                                    sort.get().getDirection().getShortName());
                        }

                        return service.findAllDetailFilter(request).data().dataList()
                                .stream();
                    }, query -> service.countAllDetailFilter(request).data().total());
                }
            };
        } catch (Exception e) {
            new ErrorNotification(e.getMessage());
            return null;
        }
    }

    @Override
    protected void save() {

        if (verifyFields()) {
            return;
        }

        try {

            CovidLoadResponse response = service.loadData(CovidLoadRequest.builder()
                            .id(responseGet != null ? responseGet.data().headerId() : null)
                            .date(dpDate.getValue()).description(tfDescription.getValue().trim())
                            .jsonString(tfJson.getValue()).jsonURL(tfJsonURL.getValue()).build(),
                    file);

            if (response == null) {
                throw new RuntimeException("No response from server");
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
            title = "Create" + " " + "Load Covid Data";
        } else if (objectToSave.id() != 0L && !view) {
            title = "Edit" + " " + "Load Covid Data" + " - ID " + objectToSave.id();
        } else if (objectToSave.id() != 0L && view) {
            title = "View" + " " + "Load Covid Data" + " - ID " + objectToSave.id();
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
            tabs.setVisible(false);
            contentDiv.setVisible(false);
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

        responseGet = service.getHeaderData(headerId);
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
            if (!securityUtils.isAccessGranted(PermitConstants.LOAD_COVID_DATA_VIEW)) {
                event.getUI().getPage().getHistory().back();
            }
        } else if (!securityUtils.isAccessGranted(PermitConstants.LOAD_COVID_DATA_EDIT)) {
            event.getUI().getPage().getHistory().back();
        }
    }

    private void buildObjectToSave(CovidLoadResponse response) {
        try {
            if (response == null) {
                throw new RuntimeException("No response from server");
            }

            if (response.data() == null && response.responseInfo() != null) {
                throw new RuntimeException(response.responseInfo().message());
            }

            objectToSave = CovidLoadRequest.builder().id(response.data().headerId())
                    .date(DateUtilities.getLocalDateFromString(response.data().loadDate(),
                            userSetting)).jsonString(response.data().jsonString())
                    .jsonURL(response.data().jsonURL()).build();

            loaded = true;
        } catch (Exception e) {
            new ErrorNotification(e.getMessage());
        }
    }
}
