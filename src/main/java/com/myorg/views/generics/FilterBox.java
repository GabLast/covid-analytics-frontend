package com.myorg.views.generics;

import com.myorg.config.security.MyVaadinSession;
import com.myorg.encapsulations.UserSetting;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.server.VaadinSession;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterBox extends Accordion {

    private         FormLayout                form;
    private final   Runnable                  callback;
    private final   Map<String, Component>    filterMap;
    private final   DatePicker.DatePickerI18n datePickerFormat;
    protected final UserSetting               settings;

    public FilterBox(Runnable callback) {
        this.callback = callback;
        this.filterMap = new HashMap<>();
        this.settings = (UserSetting) VaadinSession.getCurrent()
                .getAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString());
        this.datePickerFormat = new DatePicker.DatePickerI18n();
        configDateFormats(this.datePickerFormat);

        setId("Filters");

        add("Filters", buildView()).addThemeVariants(
                DetailsVariant.LUMO_FILLED, DetailsVariant.LUMO_REVERSE,
                DetailsVariant.LUMO_SMALL);

        setWidthFull();
    }

    private void configDateFormats(DatePicker.DatePickerI18n datePickerI18n) {
        datePickerI18n.setDateFormat(settings.dateFormat());
        datePickerI18n.setToday("Today");
        datePickerI18n.setMonthNames(List.of(
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"));
        datePickerI18n.setCancel("Cancel");
    }

    private Component buildView() {
        Button btnClearFilter =
                new Button("Clear");
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

        return form;
    }

    public Component getFilter(String id) {
        return filterMap.get(id);
    }

    public <T> void addFilter(Class<T> dataClass, String id, String caption,
            List<T> listObjects, T defaultValue, boolean multiSelect) {
        if (listObjects != null && !multiSelect) {
            Select<T> select = new Select<T>();
            select.setLabel(caption);
            select.setId(id);
            select.setItems(listObjects);
            select.setValue(defaultValue);
            select.setEmptySelectionAllowed(true);
            select.setEmptySelectionCaption("All");
            select.getElement().setAttribute("theme", "small");
            select.addValueChangeListener(
                    comboBoxTComponentValueChangeEvent -> refreshData());
            addComponentToForm(id, select);
        } else if (listObjects != null) {
            MultiSelectComboBox<T> multiSelectComboBox = new MultiSelectComboBox<T>();
            multiSelectComboBox.setLabel(caption);
            multiSelectComboBox.setId(id);
            multiSelectComboBox.setItems(listObjects);

            if (defaultValue != null) {
                multiSelectComboBox.setValue(defaultValue);
            }

            multiSelectComboBox.setAllowCustomValue(false);
            multiSelectComboBox.setPlaceholder("All");
            multiSelectComboBox.getElement().setAttribute("theme", "small");
            multiSelectComboBox.setRequired(false);
            multiSelectComboBox.setRequiredIndicatorVisible(false);
            multiSelectComboBox.setClearButtonVisible(true);
            multiSelectComboBox.addValueChangeListener(
                    comboBoxTComponentValueChangeEvent -> refreshData());
            addComponentToForm(id, multiSelectComboBox);
        } else if (dataClass == String.class) {
            TextField textField = new TextField(caption);
            textField.setId(id);
            textField.setClearButtonVisible(true);
            textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
            textField.addValueChangeListener(
                    textFieldStringComponentValueChangeEvent -> refreshData());
            addComponentToForm(id, textField);
        } else if (dataClass == Integer.class || dataClass == Long.class) {
            IntegerField numberField = new IntegerField(caption);
            numberField.setId(id);
            numberField.setClearButtonVisible(true);
            numberField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
            numberField.setPlaceholder("0");
            numberField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
            numberField.addValueChangeListener(
                    integerFieldIntegerComponentValueChangeEvent -> refreshData());
            addComponentToForm(id, numberField);
        } else if (dataClass == BigDecimal.class) {
            BigDecimalField bigDecimalField = new BigDecimalField(caption);
            bigDecimalField.setId(id);
            bigDecimalField.setClearButtonVisible(true);
            bigDecimalField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
            bigDecimalField.setPlaceholder("0");
            bigDecimalField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
            bigDecimalField.addValueChangeListener(
                    bigDecimalFieldBigDecimalComponentValueChangeEvent -> refreshData());
            addComponentToForm(id, bigDecimalField);
        } else if (dataClass == Date.class || dataClass == LocalDate.class) {
            DatePicker dpStart = new DatePicker(caption);
            dpStart.setWidthFull();
            dpStart.setId(id + "start");
            dpStart.setClearButtonVisible(true);
            dpStart.setValue(defaultValue != null ? (LocalDate) defaultValue : null);
            dpStart.getElement().setAttribute("theme", "small");
            dpStart.setLocale(UI.getCurrent().getLocale());
            dpStart.addValueChangeListener(
                    datePickerLocalDateComponentValueChangeEvent -> refreshData());
            dpStart.setI18n(datePickerFormat);

            DatePicker dpEnd = new DatePicker("To");
            dpEnd.setWidthFull();
            dpEnd.setId(id + "end");
            dpEnd.setClearButtonVisible(true);
            dpEnd.setValue(null);
            dpEnd.getElement().setAttribute("theme", "small");
            dpEnd.setLocale(UI.getCurrent().getLocale());
            dpEnd.addValueChangeListener(
                    datePickerLocalDateComponentValueChangeEvent -> refreshData());
            dpEnd.setI18n(datePickerFormat);

            FormLayout formDates = new FormLayout();
            formDates.setSizeFull();
            formDates.setResponsiveSteps(new FormLayout.ResponsiveStep("1px", 2));
            formDates.add(dpStart);
            formDates.add(dpEnd);

            addComponentToForm(id, formDates);

            filterMap.put(id + "start", dpStart);
            filterMap.put(id + "end", dpEnd);
        } else if (dataClass == LocalDateTime.class) {
            DateTimePicker dpStart = new DateTimePicker(caption);
            dpStart.setWidthFull();
            dpStart.setId(id + "start");
            dpStart.setValue(defaultValue != null ? (LocalDateTime) defaultValue : null);
            dpStart.getElement().setAttribute("theme", "small");
            dpStart.setLocale(UI.getCurrent().getLocale());
            dpStart.addValueChangeListener(
                    datePickerLocalDateComponentValueChangeEvent -> refreshData());
            dpStart.setDatePickerI18n(datePickerFormat);

            DateTimePicker dpEnd =
                    new DateTimePicker("To");
            dpEnd.setWidthFull();
            dpEnd.setId(id + "end");
            dpEnd.setValue(null);
            dpEnd.getElement().setAttribute("theme", "small");
            dpEnd.setLocale(UI.getCurrent().getLocale());
            dpEnd.addValueChangeListener(
                    datePickerLocalDateComponentValueChangeEvent -> refreshData());
            dpEnd.setDatePickerI18n(datePickerFormat);

            FormLayout formDates = new FormLayout();
            formDates.setSizeFull();
            formDates.setResponsiveSteps(new FormLayout.ResponsiveStep("1px", 1));
            formDates.add(dpStart);
            formDates.add(dpEnd);

            addComponentToForm(id, formDates);

            filterMap.put(id + "start", dpStart);
            filterMap.put(id + "end", dpEnd);
        } else if (dataClass == NativeLabel.class) {
            NativeLabel label = new NativeLabel(caption);
            label.setId(id);
            addComponentToForm(id, label);
        }
    }

    private void addComponentToForm(String id, Component component) {
        form.add(component);
        filterMap.put(id, component);
    }

    private void refreshData() {
        callback.run();
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
        } else if (component instanceof Select) {
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
}
