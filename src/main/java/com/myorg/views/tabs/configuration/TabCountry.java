package com.myorg.views.tabs.configuration;

import com.myorg.config.security.MyVaadinSession;
import com.myorg.dto.request.configurations.CountryFilterRequest;
import com.myorg.dto.response.configuration.CountryFilterDataDetails;
import com.myorg.encapsulations.User;
import com.myorg.encapsulations.UserSetting;
import com.myorg.service.CovidAnalyticsService;
import com.myorg.utils.PermitConstants;
import com.myorg.utils.SecurityUtils;
import com.myorg.utils.Utilities;
import com.myorg.views.MainLayout;
import com.myorg.views.forms.configuration.FormCountry;
import com.myorg.views.generics.dialog.ConfirmWindow;
import com.myorg.views.generics.notifications.ErrorNotification;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import org.vaadin.crudui.crud.LazyCrudListener;
import org.vaadin.crudui.crud.impl.GridCrud;

import java.util.Optional;

@Route(value = "configuration/country", layout = MainLayout.class)
public class TabCountry extends Div
        implements AfterNavigationObserver, HasDynamicTitle {

    private final User        user;
    private final UserSetting userSetting;

    private final CovidAnalyticsService service;
    private final SecurityUtils         securityUtils;
    private       MenuItem              miRefresh, miCreate, miEdit, miView, miDelete;

    //filters
    private FormLayout                     form;
    private TextField      nameFilter;
    private TextField      codeFilter;
    private Select<String> enabledFilter;

    private GridCrud<CountryFilterDataDetails> gridCrud;
    //Selected Object
    private CountryFilterDataDetails           object = null;

    private Registration broadcaster;

    public TabCountry(CovidAnalyticsService service, SecurityUtils securityUtils) {
        this.user = (User) VaadinSession.getCurrent()
                .getAttribute(MyVaadinSession.SessionVariables.USER.toString());
        this.userSetting = (UserSetting) VaadinSession.getCurrent()
                .getAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString());
        this.service = service;
        this.securityUtils = securityUtils;

        prepareComponents();
    }

    private void prepareComponents() {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setSpacing(true);
        content.add(configFilters());

        configGrid();
        content.add(configButtons());
        content.add(gridCrud.getGrid());

        //Security
        applySecurity();

        add(content);
        setSizeFull();
    }

    private Component configFilters() {

        nameFilter = new TextField("Name");
        nameFilter.setClearButtonVisible(true);
        nameFilter.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        nameFilter.addValueChangeListener(e -> refreshData());

        codeFilter = new TextField("Code");
        codeFilter.setClearButtonVisible(true);
        codeFilter.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        codeFilter.addValueChangeListener(e -> refreshData());

        enabledFilter = new Select<>();
        enabledFilter.setLabel("Enabled");
        enabledFilter.setItems(Utilities.listBooleanYesNo());
        enabledFilter.setValue("Yes");
        enabledFilter.setEmptySelectionAllowed(false);
        enabledFilter.setEmptySelectionCaption("All");
        enabledFilter.getElement().setAttribute("theme", "small");
        enabledFilter.addValueChangeListener(e -> refreshData());

        Accordion accordion = new Accordion();
        accordion.setWidthFull();
        accordion.add("Filters", buildFilterBox())
                .addThemeVariants(DetailsVariant.LUMO_FILLED, DetailsVariant.LUMO_REVERSE,
                        DetailsVariant.LUMO_SMALL);

        return accordion;
    }

    private Component buildFilterBox() {
        Button btnClearFilter = new Button("Clear");
        btnClearFilter.setIcon(new Icon(VaadinIcon.CROP));
        btnClearFilter.addThemeVariants(ButtonVariant.LUMO_SMALL);
        btnClearFilter.addClickListener(buttonClickEvent -> clearAll());

        form = new FormLayout();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("300px", 2),
                new FormLayout.ResponsiveStep("600px", 3),
                new FormLayout.ResponsiveStep("900px", 4),
                new FormLayout.ResponsiveStep("1200px", 5));
        form.add(btnClearFilter, 1);
        form.add(new Div(), 4);

        form.add(nameFilter, codeFilter, enabledFilter);

        return form;
    }

    private void applySecurity() {
        miRefresh.setVisible(true);
        miCreate.setVisible(securityUtils.isAccessGranted(PermitConstants.COUNTRY_CREATE));
        miEdit.setVisible(securityUtils.isAccessGranted(PermitConstants.COUNTRY_EDIT));
        miView.setVisible(securityUtils.isAccessGranted(PermitConstants.COUNTRY_VIEW));
        miDelete.setVisible(false);
//        miDelete.setVisible(securityUtils.isAccessGranted(PermitConstants.COUNTRY_DELETE));
    }

    private Component configButtons() {
        MenuBar toolBar = new MenuBar();
        toolBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE,
                MenuBarVariant.LUMO_LARGE);
        toolBar.setWidthFull();

        Button btnCreate = new Button("Create", new Icon(VaadinIcon.PLUS));
        btnCreate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnCreate.setSizeFull();

        Button btnView = new Button("View", new Icon(VaadinIcon.SEARCH));
        btnView.setSizeFull();

        Button btnEdit = new Button("Edit", new Icon(VaadinIcon.PENCIL));
        btnEdit.setSizeFull();

        Button btnDelete = new Button("Delete", new Icon(VaadinIcon.TRASH));
        btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btnDelete.setSizeFull();

        gridCrud.setRowCountCaption("%d value(s) found");
        gridCrud.getFindAllButton().setText("Refresh");
        miRefresh = toolBar.addItem(gridCrud.getFindAllButton());

        miCreate = toolBar.addItem(btnCreate,
                e -> UI.getCurrent().navigate(FormCountry.class));

        miEdit = toolBar.addItem(btnEdit, e -> {
            RouteParameters parameters =
                    new RouteParameters(new RouteParam("id", object.id().toString()),
                            new RouteParam("view", "0"));

            UI.getCurrent().navigate(FormCountry.class, parameters);
        });

        miView = toolBar.addItem(btnView, e -> {
            RouteParameters parameters =
                    new RouteParameters(new RouteParam("id", object.id().toString()),
                            new RouteParam("view", "1"));

            UI.getCurrent().navigate(FormCountry.class, parameters);
        });

        miDelete = toolBar.addItem(btnDelete, e -> {

            ConfirmWindow confirmWindow =
                    new ConfirmWindow("Are you sure you want to continue?", this::delete);
            confirmWindow.open();
        });

        return toolBar;
    }

    private void configGrid() {
        gridCrud = new GridCrud<>(CountryFilterDataDetails.class);

        Grid<CountryFilterDataDetails> grid = gridCrud.getGrid();
        grid.removeAllColumns();

        grid.setSizeFull();
        grid.setPageSize(50);
        grid.setMultiSort(false);
        grid.setColumnReorderingAllowed(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);

        grid.addColumn(CountryFilterDataDetails::id).setKey("id").setHeader("ID")
                .setSortable(true).setResizable(true).setFlexGrow(1);
        grid.addColumn(CountryFilterDataDetails::name).setKey("name")
                .setHeader("Name").setSortable(true).setResizable(true)
                .setFlexGrow(1);
        grid.addColumn(CountryFilterDataDetails::code).setKey("countryCode")
                .setHeader("Country Code").setSortable(true).setResizable(true)
                .setFlexGrow(1);
        grid.addColumn(CountryFilterDataDetails::placeId).setKey("placeId")
                .setHeader("Place ID").setSortable(false).setResizable(true)
                .setFlexGrow(1);
        grid.addColumn(CountryFilterDataDetails::wikiDataId).setKey("wikiDataId")
                .setHeader("Wiki Data ID").setSortable(false).setResizable(true)
                .setFlexGrow(1);
        grid.addColumn(CountryFilterDataDetails::dataCommonsId).setKey("dataCommonsId")
                .setHeader("Data Commons ID").setSortable(false).setResizable(true)
                .setFlexGrow(1);
        grid.addColumn(CountryFilterDataDetails::iso_3166_1_alpha_2).setKey("iso_3166_1_alpha_2")
                .setHeader("ISO-3166 Alpha 2").setSortable(false).setResizable(true)
                .setFlexGrow(1);
        grid.addColumn(CountryFilterDataDetails::iso_3166_1_alpha_3).setKey("iso_3166_1_alpha_3")
                .setHeader("ISO-3166 Alpha 3").setSortable(false).setResizable(true)
                .setFlexGrow(1);

        grid.addSelectionListener(selectionEvent -> {
            Optional<CountryFilterDataDetails> firstSelectedItem =
                    selectionEvent.getFirstSelectedItem();
            if (firstSelectedItem.isPresent()) {
                object = firstSelectedItem.get();
                modifyBtnState(true);
            } else {
                object = null;
                modifyBtnState(false);
            }
        });
        gridCrud.setCrudListener(configDataSource());
        gridCrud.refreshGrid();
    }

    private LazyCrudListener<CountryFilterDataDetails> configDataSource() {
        try {
            return new LazyCrudListener<>() {
                @Override
                public CountryFilterDataDetails add(
                        CountryFilterDataDetails covidHeaderFilterDataDetails) {
                    return null;
                }

                @Override
                public CountryFilterDataDetails update(
                        CountryFilterDataDetails covidHeaderFilterDataDetails) {
                    return null;
                }

                @Override
                public void delete(
                        CountryFilterDataDetails covidHeaderFilterDataDetails) {
                }

                @Override
                public DataProvider<CountryFilterDataDetails, ?> getDataProvider() {
                    CountryFilterRequest request = new CountryFilterRequest();
                    request.setName(nameFilter.getValue().trim());
                    request.setCountryCode(codeFilter.getValue().trim());
                    request.setEnabled(
                            enabledFilter.getValue() != null && enabledFilter.getValue()
                                    .equalsIgnoreCase("yes"));

                    return DataProvider.fromCallbacks(query -> {
                        request.setLimit(query.getLimit());
                        request.setOffset(query.getOffset());

                        Optional<QuerySortOrder> sort =
                                query.getSortOrders().stream().findAny();
                        if (sort.isPresent()) {
                            request.setSortProperty(sort.get().getSorted());
                            request.setSortOrder(sort.get().getDirection().getShortName());
                        }

                        return service.filterCountries(request).data().dataList()
                                .stream();
                    }, query -> service.countCountriesFilter(request).data().total());
                }
            };
        } catch (Exception e) {
            new ErrorNotification(e.getMessage());
            return null;
        }
    }

    private void delete() {

        if (object == null) {
            return;
        }

        try {

//            ProfileResponse response = service.deleteProfile(object.id());
//
//            if (response == null) {
//                throw new RuntimeException("No response from server");
//            }
//
//            if (response.data() == null && response.responseInfo() != null) {
//                throw new RuntimeException(response.responseInfo().message());
//            }
//
//            new SuccessNotification("The data has been successfully deleted");
//            gridCrud.refreshGrid();

        } catch (Exception e) {
            new ErrorNotification(e.getMessage());
        }
    }

    private void modifyBtnState(boolean status) {
        miEdit.setEnabled(status);
        miView.setEnabled(status);
        miDelete.setEnabled(status);
    }

    private void refreshData() {
        gridCrud.refreshGrid();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        modifyBtnState(false);
    }

    private void clearAll() {
        form.getChildren().forEach(component -> {
            if (component instanceof HorizontalLayout
                    || component instanceof VerticalLayout
                    || component instanceof FormLayout) {
                component.getChildren().forEach(this::clearComponent);
            } else {
                clearComponent(component);
            }
        });
    }

    private void clearComponent(Component component) {

        if (component instanceof HasValueAndElement
                && ((HasValueAndElement<?, ?>) component).isReadOnly()) {
            return;
        }

        if (component instanceof TextField) {
            ((TextField) component).clear();
        } else if (component instanceof IntegerField) {
            ((IntegerField) component).clear();
        } else if (component instanceof BigDecimalField) {
            ((BigDecimalField) component).clear();
        } else if (component instanceof Select && ((Select<?>) component).isEmptySelectionAllowed()) {
            ((Select<?>) component).clear();
        } else if (component instanceof ComboBox) {
            ((ComboBox<?>) component).clear();
        } else if (component instanceof DatePicker) {
            ((DatePicker) component).clear();
        } else if (component instanceof MultiSelectComboBox) {
            ((MultiSelectComboBox<?>) component).clear();
        } else if (component instanceof DateTimePicker) {
            ((DateTimePicker) component).clear();
        }
    }

    @Override
    public String getPageTitle() {
        return "Countries";
    }
}
