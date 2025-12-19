package com.myorg.views.tabs.process;

import com.myorg.dto.response.module.CovidData;
import com.myorg.service.CovidAnalyticsService;
import com.myorg.utils.Utilities;
import com.myorg.views.MainLayout;
import com.myorg.views.generics.FilterBox;
import com.myorg.views.generics.GenericTab;
import com.myorg.views.generics.dialog.ConfirmWindow;
import com.myorg.views.generics.notifications.ErrorNotification;
import com.myorg.views.generics.notifications.SuccessNotification;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.shared.Registration;
import jakarta.annotation.security.RolesAllowed;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.crud.LazyCrudListener;
import org.vaadin.crudui.crud.impl.GridCrud;
import org.vaadin.crudui.form.impl.form.factory.DefaultCrudFormFactory;
import org.vaadin.crudui.layout.impl.WindowBasedCrudLayout;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Route(value = "process/covidload", layout = MainLayout.class)
public class TabLoadData extends GenericTab<CovidData> implements HasDynamicTitle {

    private final CovidAnalyticsService service;

    private MenuItem miCreate, miEdit, miView, miDelete;

    private Registration broadcaster;

    public TabLoadData(CovidAnalyticsService service) {
        super(CovidData.class, false);
        this.service = service;

        prepareComponets();
    }

    @Override
    protected void applySecurity(GridCrud<TestData> crud) {
        crud.setFindAllOperationVisible(true);
        crud.setAddOperationVisible(SecurityUtils.isAccessGranted(Permit.TEST_DATA_CREATE));
        crud.setUpdateOperationVisible(SecurityUtils.isAccessGranted(Permit.TEST_DATA_EDIT));
        menuVisualizar.setVisible(SecurityUtils.isAccessGranted(Permit.TEST_DATA_VIEW));
        crud.setDeleteOperationVisible(SecurityUtils.isAccessGranted(Permit.TEST_DATA_DELETE));

        miCreate.setVisible(SecurityUtils.isAccessGranted(Permit.TEST_DATA_CREATE));
        miEdit.setVisible(SecurityUtils.isAccessGranted(Permit.TEST_DATA_EDIT));
        miView.setVisible(SecurityUtils.isAccessGranted(Permit.TEST_DATA_VIEW));
        miDelete.setVisible(SecurityUtils.isAccessGranted(Permit.TEST_DATA_DELETE));
    }

    @Override
    protected void configFilters(FilterBox filterBox) {
        filterBox.addFilter(String.class, "word", UI.getCurrent().getTranslation("tab.testdata.word"), null, null, false);
        filterBox.addFilter(TestType.class, "testType", UI.getCurrent().getTranslation("tab.testdata.testtype"), testTypeService.findAllByEnabled(true), null, false);
        filterBox.addFilter(String.class, "description", UI.getCurrent().getTranslation("tab.testdata.description"), null, null, false);
        filterBox.addFilter(Date.class, "date", UI.getCurrent().getTranslation("tab.testdata.date"), null, null, false);
    }

    @Override
    protected void configCrudUIMessages(WindowBasedCrudLayout crudLayout) {
        crudLayout.setWindowCaption(CrudOperation.ADD, UI.getCurrent().getTranslation("create"));
        crudLayout.setWindowCaption(CrudOperation.UPDATE, UI.getCurrent().getTranslation("edit"));
        crudLayout.setWindowCaption(CrudOperation.DELETE, UI.getCurrent().getTranslation("delete"));

        gridCrud.setDeletedMessage(UI.getCurrent().getTranslation("action.delete"));
        gridCrud.setSavedMessage(UI.getCurrent().getTranslation("action.save"));
        gridCrud.setRowCountCaption(UI.getCurrent().getTranslation("action.rowcount", UI.getCurrent().getTranslation("data")));
    }

    @Override
    protected void configButtons(MenuBar toolBar) {
        Button btnCreate = new Button(UI.getCurrent().getTranslation("create.object", UI.getCurrent().getTranslation("data")), new Icon(VaadinIcon.PLUS));
        btnCreate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnCreate.setSizeFull();

        Button btnView = new Button(UI.getCurrent().getTranslation("view.object", UI.getCurrent().getTranslation("data")), new Icon(VaadinIcon.SEARCH));
        btnView.setSizeFull();

        Button btnEdit = new Button(UI.getCurrent().getTranslation("edit.object", UI.getCurrent().getTranslation("data")), new Icon(VaadinIcon.PENCIL));
        btnEdit.setSizeFull();

        Button btnDelete = new Button(UI.getCurrent().getTranslation("delete.object", UI.getCurrent().getTranslation("data")), new Icon(VaadinIcon.TRASH));
        btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btnDelete.setSizeFull();

        miCreate = toolBar.addItem(
                btnCreate, e -> UI.getCurrent().navigate(FormTestData.class));

        miEdit = toolBar.addItem(btnEdit, e -> {
            RouteParameters parameters = new RouteParameters(
                    new RouteParam("id", object.getId().toString()),
                    new RouteParam("view", "0")
            );

            UI.getCurrent().navigate(FormTestData.class, parameters);
        });

        miView = toolBar.addItem(btnView, e -> {
            RouteParameters parameters = new RouteParameters(
                    new RouteParam("id", object.getId().toString()),
                    new RouteParam("view", "1")
            );

            UI.getCurrent().navigate(FormTestData.class, parameters);
        });

        miDelete = toolBar.addItem(btnDelete, e -> {

            ConfirmWindow confirmWindow =
                    new ConfirmWindow(
                            UI.getCurrent().getTranslation("action.confirm.question", object.toString()),
                            this::delete);
            confirmWindow.open();
        });
    }

    @Override
    protected void configGrid(Grid<TestData> grid) {
        grid.removeAllColumns();
        grid.addColumn(TestData::getWord).setKey("word").setHeader(UI.getCurrent().getTranslation("tab.testdata.word")).setSortable(true).setResizable(true).setFlexGrow(0).setWidth("12rem");
        grid.addColumn(data -> UI.getCurrent().getTranslation(TestType.toStringI18nKey(data.getTestType()))).setKey("testType").setHeader(UI.getCurrent().getTranslation("tab.testdata.testtype")).setSortable(true).setResizable(true).setFlexGrow(0).setWidth("7.5rem");
        grid.addColumn(data -> Utilities.formatDate(data.getDate(), userSetting.getDateTimeFormat(), userSetting.getTimeZoneString())).setKey("date").setHeader(UI.getCurrent().getTranslation("tab.testdata.date")).setSortable(true).setResizable(true).setFlexGrow(0).setWidth("10.625rem");
        grid.addColumn(TestData::getDescription).setKey("description").setHeader(UI.getCurrent().getTranslation("tab.testdata.description")).setSortable(true).setResizable(true).setFlexGrow(1);
        grid.addColumn(data -> Utilities.formatDecimal(data.getNumber())).setKey("number").setHeader(UI.getCurrent().getTranslation("tab.testdata.number")).setSortable(true).setResizable(true).setFlexGrow(1);
    }

    @Override
    protected LazyCrudListener<TestData> configDataSource() {
        return new LazyCrudListener<>() {
            @Override
            public DataProvider<TestData, ?> getDataProvider() {
                TextField word = (TextField) filterBox.getFilter("word");
                TextField description = (TextField) filterBox.getFilter("description");
                Select<TestType> type = (Select<TestType>) filterBox.getFilter("testType");
                DatePicker start = (DatePicker) filterBox.getFilter("datestart");
                DatePicker end = (DatePicker) filterBox.getFilter("dateend");

                return DataProvider.fromCallbacks(
                        query -> testDataService.findAllFilter(
                                true,
                                userSetting.getTimeZoneString(),
                                word.getValue(),
                                description.getValue(),
                                type.getValue(),
                                null,
                                start.getValue(), end.getValue(),
                                query.getLimit(),
                                query.getOffset(),
                                Utilities.generarSortDeBusquedaTabla(
                                        TestData.class,
                                        gridCrud.getGrid().getSortOrder(),
                                        Sort.by(Sort.Direction.DESC, "id"),
                                        null
                                )
                        ).stream(),
                        query -> testDataService.countAllFilter(
                                true,
                                userSetting.getTimeZoneString(),
                                word.getValue(),
                                description.getValue(),
                                type.getValue(),
                                start.getValue(), end.getValue()
                        )
                );
            }

            @Override
            public TestData add(TestData data) {
                if (StringUtils.isBlank(data.getWord())) {
                    throw new MyException(UI.getCurrent().getTranslation("form.error"));
                }

                if (StringUtils.isBlank(data.getDescription())) {
                    throw new MyException(UI.getCurrent().getTranslation("form.error"));
                }

                if(data.getDate() == null) {
                    throw new MyException(UI.getCurrent().getTranslation("form.error"));
                }

                if(data.getNumber() == null) {
                    throw new MyException(UI.getCurrent().getTranslation("form.error"));
                }

                if(data.getTestType() == null) {
                    throw new MyException(UI.getCurrent().getTranslation("form.error"));
                }

                return testDataService.saveAndFlush(data);
            }

            @Override
            public TestData update(TestData data) {
                if (StringUtils.isBlank(data.getWord())) {
                    throw new MyException(UI.getCurrent().getTranslation("form.error"));
                }

                if (StringUtils.isBlank(data.getDescription())) {
                    throw new MyException(UI.getCurrent().getTranslation("form.error"));
                }

                if(data.getDate() == null) {
                    throw new MyException(UI.getCurrent().getTranslation("form.error"));
                }

                if(data.getNumber() == null) {
                    throw new MyException(UI.getCurrent().getTranslation("form.error"));
                }

                if(data.getTestType() == null) {
                    throw new MyException(UI.getCurrent().getTranslation("form.error"));
                }

                return testDataService.saveAndFlush(data);
            }

            @Override
            public void delete(TestData data) {
                testDataService.disable(data);
            }
        };
    }


    @Override
    protected void buildFromCruidUI(DefaultCrudFormFactory<TestData> formFactory) {
        formFactory.setVisibleProperties("word", "testType", "description", "number", "date");
        formFactory.setFieldCaptions(
                UI.getCurrent().getTranslation("tab.testdata.word"),
                UI.getCurrent().getTranslation("tab.testdata.testtype"),
                UI.getCurrent().getTranslation("tab.testdata.description"),
                UI.getCurrent().getTranslation("tab.testdata.number"),
                UI.getCurrent().getTranslation("tab.testdata.date")
        );

        formFactory.setFieldProvider("word", a -> {
            TextField field = new TextField(UI.getCurrent().getTranslation("tab.testdata.word"));
            field.setPlaceholder(UI.getCurrent().getTranslation("tab.testdata.word") + "...");
            field.setRequired(true);
            field.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
            field.setRequiredIndicatorVisible(true);
            return field;
        });

        formFactory.setFieldProvider("testType", a -> {
            ComboBox<TestType> select = new ComboBox<>();
            select.setLabel(UI.getCurrent().getTranslation("tab.testdata.testtype"));
            select.setPlaceholder(UI.getCurrent().getTranslation("tab.testdata.testtype") + "...");
            select.setRequiredIndicatorVisible(true);
            select.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
            select.setItems(testTypeService.findAllByEnabled(true));
            select.setItemLabelGenerator(data -> UI.getCurrent().getTranslation(TestType.toStringI18nKey(data)));
            return select;
        });

        formFactory.setFieldProvider("description", a -> {
            TextArea field = new TextArea(UI.getCurrent().getTranslation("tab.testdata.description"));
            field.setPlaceholder(UI.getCurrent().getTranslation("tab.testdata.description") + "...");
            field.setRequired(true);
            field.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
            field.setRequiredIndicatorVisible(true);
            return field;
        });

        formFactory.setFieldProvider("date", a -> {
            DateTimePicker dpDate = new DateTimePicker(UI.getCurrent().getTranslation("form.testdata.date"));
            dpDate.setRequiredIndicatorVisible(true);
            dpDate.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
            dpDate.setLocale(userSetting.getLocale());
            dpDate.setSizeFull();
            return dpDate;
        });

        formFactory.setFieldProvider("number", object1 -> {
            BigDecimalField decimalField = new BigDecimalField("number");
            decimalField.setRequired(true);
            decimalField.setErrorMessage(UI.getCurrent().getTranslation("form.error"));
            decimalField.setRequiredIndicatorVisible(false);
            return decimalField;
        });

        formFactory.setConverter("number", new Converter<BigDecimal, BigDecimal>() {
            @Override
            public Result<BigDecimal> convertToModel(BigDecimal value, ValueContext valueContext) {
                return Result.ok(Objects.requireNonNullElse(value, BigDecimal.ZERO));
            }

            @Override
            public BigDecimal convertToPresentation(BigDecimal value, ValueContext valueContext) {
                return Objects.requireNonNullElse(value, BigDecimal.ZERO);
            }
        });
        formFactory.setConverter("date", new Converter<LocalDateTime, Date>() {
            @Override
            public Result<Date> convertToModel(LocalDateTime value, ValueContext valueContext) {
                return Result.ok(Objects.requireNonNullElse(Date.from(value.atZone(userSetting.getTimezone().toZoneId()).toInstant()), null));
            }

            @Override
            public LocalDateTime convertToPresentation(Date value, ValueContext valueContext) {
                if (value == null) {
                    value = new Date();
                }
                return Objects.requireNonNullElse(value.toInstant().atZone(userSetting.getTimezone().toZoneId()).toLocalDateTime(), null);
            }
        });
    }

    @Override
    protected void configOtherComponents(Div divCustomizeBar) {
        Optional<Integer> redisTest = redisTestRedisService.getSimpleDataCountCache();
        Span span;
        span = redisTest.map(
                        test -> new Span(UI.getCurrent().getTranslation("redis.testdata.count", test)))
                .orElseGet(() -> new Span(UI.getCurrent().getTranslation("redis.testdata.count", UI.getCurrent().getTranslation("empty"))));

        divCustomizeBar.add(span);
        divCustomizeBar.setVisible(true);
    }

    @Override
    protected void modifyBtnState() {
        if (object != null) {

//            System.out.println("JSON:");
//            System.out.println(new Gson().toJson(object, TestData.class));
//            RestRequestGet a = new RestRequestGet();
//            a.setId(1L);
//            System.out.println(new Gson().toJson(a, RestRequestGet.class));
            miView.setEnabled(true);
            if (object.isEnabled()) {
                miEdit.setEnabled(true);
                miDelete.setEnabled(true);
            } else {
                miDelete.setEnabled(false);
                miEdit.setEnabled(false);
            }
        } else {
            miView.setEnabled(false);
            miEdit.setEnabled(false);
            miDelete.setEnabled(false);
        }
    }

    @Override
    protected void delete() {
        try {
            if (object == null) {
                return;
            }

            testDataService.disable(object);
            gridCrud.refreshGrid();

            new SuccessNotification(UI.getCurrent().getTranslation("action.delete"));
        } catch (MyException e) {
            new ErrorNotification(e.getMessage());
        }
    }

    @Override
    public String getPageTitle() {
        return UI.getCurrent().getTranslation("title.testdata");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        broadcaster = BroadcasterTestData.getInstance().register(data -> {
            if (attachEvent.getUI().isClosing()) {
                return;
            }

            attachEvent.getUI().access(() -> {
                if (data.getId() != 0L) {
                    gridCrud.refreshGrid();
                }
            });
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        broadcaster.remove();
        broadcaster = null;
    }
}
