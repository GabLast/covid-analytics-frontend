package com.myorg.views.general;

import com.myorg.config.security.MyVaadinSession;
import com.myorg.dto.request.dashboard.DashboardTwoFilterRequest;
import com.myorg.dto.response.configuration.CountryFindAllDataDetails;
import com.myorg.dto.response.configuration.CountryFindAllResponse;
import com.myorg.dto.response.dashboard.DashboardOneData;
import com.myorg.dto.response.dashboard.DashboardOneDataDetails;
import com.myorg.dto.response.dashboard.DashboardOneResponse;
import com.myorg.dto.response.dashboard.DashboardTwoData;
import com.myorg.dto.response.dashboard.DashboardTwoResponse;
import com.myorg.encapsulations.User;
import com.myorg.encapsulations.UserSetting;
import com.myorg.service.CovidAnalyticsService;
import com.myorg.utils.GlobalConstants;
import com.myorg.utils.PermitConstants;
import com.myorg.utils.SecurityUtils;
import com.myorg.views.MainLayout;
import com.myorg.views.generics.notifications.ErrorNotification;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.SelectVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import software.xdev.chartjs.model.charts.BarChart;
import software.xdev.chartjs.model.data.BarData;
import software.xdev.chartjs.model.dataset.BarDataset;
import software.xdev.chartjs.model.options.BarOptions;
import software.xdev.chartjs.model.options.Plugins;
import software.xdev.chartjs.model.options.Title;
import software.xdev.vaadin.chartjs.ChartContainer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Route(value = "dashboard", layout = MainLayout.class)
@Slf4j
public class TabDashboard extends VerticalLayout implements HasDynamicTitle {
    //samples: https://github.com/xdev-software/vaadin-chartjs-wrapper/tree/develop

    private final User        user;
    private final UserSetting userSetting;

    private final CovidAnalyticsService service;
    private final SecurityUtils         securityUtils;

    private Tabs tabs;
    private Tab  tabBoardOne, tabBoardTwo;
    private VerticalLayout content;

    private ChartContainer chartOneContainer;
    private ChartContainer chartTwoContainer;

    private FormLayout                        formFilters;
    private Select<CountryFindAllDataDetails> countryFilter;
    private DatePicker                        startFilter, endFilter;

    private DatePicker.DatePickerI18n datePickerFormat = new DatePicker.DatePickerI18n();

    public TabDashboard(CovidAnalyticsService service, SecurityUtils securityUtils) {
        this.user = (User) VaadinSession.getCurrent()
                .getAttribute(MyVaadinSession.SessionVariables.USER.toString());
        this.userSetting = (UserSetting) VaadinSession.getCurrent()
                .getAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString());
        this.service = service;
        this.securityUtils = securityUtils;
        this.datePickerFormat.setDateFormat(userSetting.dateFormat());

        setSizeFull();
        setMargin(false);
        setPadding(false);
        buildDashboard();
    }

    private void buildDashboard() {
        setHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();
        setMargin(false);
        setPadding(false);
        getStyle().set("background-repeat", "no-repeat");
        getStyle().set("background-size", "cover");
        getStyle().set("background-position", "center");

        content = new VerticalLayout();
        content.setSizeFull();

        buildBoardTwoFilters();

        setComponentValues();

        tabBoardOne = new Tab("General Information");
        tabBoardOne.setVisible(
                securityUtils.isAccessGranted(PermitConstants.DASHBOARD_TAB_ONE));

        tabBoardTwo = new Tab("Filtered Information");
        tabBoardTwo.setVisible(
                securityUtils.isAccessGranted(PermitConstants.DASHBOARD_TAB_TWO));

        tabs = new Tabs(tabBoardOne, tabBoardTwo);
        tabs.setWidthFull();

        add(tabs);
        add(content);

        tabs.setAutoselect(true);
        tabs.addSelectedChangeListener(event -> {

            if (event.getSelectedTab() == null) {
                return;
            }

            if (event.getSelectedTab().equals(tabBoardOne)) {
                showBoardOne();
            } else if (event.getSelectedTab().equals(tabBoardTwo)) {
                showBoardTwo();
            }
        });

        tabs.setSelectedTab(null);
        if (tabBoardOne.isVisible()) {
            tabs.setSelectedTab(tabBoardOne);
        } else if (tabBoardOne.isVisible()) {
            tabs.setSelectedTab(tabBoardTwo);
        }
    }

    private void showBoardOne() {
        content.removeAll();

        content.getStyle().set("padding-top", "10px");
        content.getStyle().set("padding-left", "20px");
        content.getStyle().set("padding-right", "20px");

        chartOneContainer = new ChartContainer();
        chartOneContainer.setHeight("70%");
        content.add(chartOneContainer);

        loadDashboardOne();
    }

    private void loadDashboardOne() {
        chartOneContainer.showLoading();
        final UI ui = UI.getCurrent();
        CompletableFuture.runAsync(() -> {
            ui.access(this::buildBoardOne);
        });
    }

    private void buildBoardOne() {

        DashboardOneData data = fetchDashboardOneData();

        List<BarDataset> datasets = new ArrayList<>();
        LinkedList<String> availableColors = new LinkedList<>(GlobalConstants.COLOR_LIST);

        for (DashboardOneDataDetails detail : data.details()) {
            BarDataset dataset = new BarDataset().setLabel(detail.country())
                    .setBackgroundColor(availableColors.isEmpty() ? "#ffffff"
                                                                  : availableColors.removeFirst())
                    .addData(detail.population()).addData(detail.populationMale())
                    .addData(detail.populationFemale());
            datasets.add(dataset);
        }

        if(CollectionUtils.isEmpty(datasets)) {
            BarDataset dataset = new BarDataset().setLabel("No Data")
                    .setBackgroundColor(availableColors.isEmpty() ? "#ffffff"
                                                                  : availableColors.removeFirst())
                    .addData(0).addData(0)
                    .addData(0);
            datasets.add(dataset);
        }

        String json = new BarChart(new BarData()
                .addLabels("Population Total", "Population Male", "Population Female")
                .setDatasets(datasets)).setOptions(
                new BarOptions().setResponsive(true).setMaintainAspectRatio(false)
                        .setPlugins(new Plugins().setTitle(
                                new Title().setText("COVID-19 Dataset")
                                        .setDisplay(true)))).toJson();

        chartOneContainer.showChart(json);
    }

    private void showBoardTwo() {
        content.removeAll();

        content.getStyle().set("padding-top", "10px");
        content.getStyle().set("padding-left", "20px");
        content.getStyle().set("padding-right", "20px");

        chartTwoContainer = new ChartContainer();
        chartTwoContainer.setHeight("60%");

        content.add(formFilters);
        content.add(chartTwoContainer);

        loadDashboardTwo();
    }

    private void loadDashboardTwo() {
        chartTwoContainer.showLoading();
        final UI ui = UI.getCurrent();
        CompletableFuture.runAsync(() -> {
            ui.access(this::buildBoardTwo);
        });
    }

    private void buildBoardTwo() {
        DashboardTwoData data = fetchDashboardTwoData();
        String json = new BarChart(new BarData()
                .addLabels("Infections", "Deaths", "Partial Vaccinations",
                        "Full Vaccinations", "Vaccine Doses Administrated", "New Tested")
                .addDataset(
                        new BarDataset().setLabel("Amount of People")
                                .setBackgroundColor("#ffa64d")
                                .addData(data.infections())
                                .addData(data.deaths())
                                .addData(data.newPersonVaccinated())
                                .addData(data.newPersonFullyVaccinated())
                                .addData(data.newVaccineDosesAdministered())
                                .addData(data.newTested())
                ))
                .setOptions(new BarOptions().setResponsive(true).setMaintainAspectRatio(false)
                .setPlugins(new Plugins().setTitle(
                        new Title().setText("COVID-19 Filtered Dataset")
                                .setDisplay(true)))).toJson();

        chartTwoContainer.showChart(json);
    }

    private void buildBoardTwoFilters() {

        startFilter = new DatePicker("Start");
        startFilter.setI18n(datePickerFormat);
        startFilter.setWidthFull();
        startFilter.setClearButtonVisible(true);
        startFilter.getElement().setAttribute("theme", "small");
        startFilter.addValueChangeListener(e -> loadDashboardTwo());
        startFilter.addThemeVariants(DatePickerVariant.LUMO_SMALL);

        endFilter = new DatePicker("End");
        endFilter.setI18n(datePickerFormat);
        endFilter.setWidthFull();
        endFilter.setClearButtonVisible(true);
        endFilter.getElement().setAttribute("theme", "small");
        endFilter.addValueChangeListener(e -> loadDashboardTwo());
        endFilter.addThemeVariants(DatePickerVariant.LUMO_SMALL);

        countryFilter = new Select<>();
        countryFilter.setLabel("Country");
        countryFilter.setEmptySelectionAllowed(true);
        countryFilter.setEmptySelectionCaption("All");
        countryFilter.getElement().setAttribute("theme", "small");
        countryFilter.addValueChangeListener(e -> loadDashboardTwo());
        countryFilter.addThemeVariants(SelectVariant.LUMO_SMALL);

        formFilters = new FormLayout();
        formFilters.setSizeUndefined();
        formFilters.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2),
                new FormLayout.ResponsiveStep("900px", 3),
                new FormLayout.ResponsiveStep("1200px", 4));

        formFilters.add(countryFilter, 1);
        formFilters.add(startFilter, 1);
        formFilters.add(endFilter, 1);
    }

    private void setComponentValues() {
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

    private DashboardOneData fetchDashboardOneData() {
        try {
            DashboardOneResponse response = service.getBoardOneData();

            if (response == null) {
                throw new RuntimeException("No response from server");
            }

            if (response.data() == null && response.responseInfo() != null) {
                throw new RuntimeException(response.responseInfo().message());
            }

            return response.data();
        } catch (Exception e) {
            new ErrorNotification(e.getMessage());
        }
        return null;
    }

    private DashboardTwoData fetchDashboardTwoData() {
        try {

            DashboardTwoFilterRequest request = DashboardTwoFilterRequest.builder()
                    .country(countryFilter.getValue() != null ? countryFilter.getValue()
                            .code() : null).dateStart(startFilter.getValue())
                    .dateEnd(endFilter.getValue()).build();

            DashboardTwoResponse response = service.getBoardTwoData(request);

            if (response == null) {
                throw new RuntimeException("No response from server");
            }

            if (response.data() == null && response.responseInfo() != null) {
                throw new RuntimeException(response.responseInfo().message());
            }

            return response.data();
        } catch (Exception e) {
            new ErrorNotification(e.getMessage());
        }
        return null;
    }

    @Override
    public String getPageTitle() {
        return "Dashboard";
    }
}
