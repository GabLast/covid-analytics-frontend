package com.myorg.utils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class Utilities {

    public static Logger getLogger(Class<?> aClass) {
        return LoggerFactory.getLogger(aClass);
    }


    public static <T> T setFieldValue(T entidad, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field;
        try {
            field = entidad.getClass().getSuperclass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException ignored) {
            try {
                field = entidad.getClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                throw e;
            }
        }
        field.setAccessible(true);
        field.set(entidad, value);
        return entidad;
    }

    public static <T> Object getFieldValue(T object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field;
        try {
            field = object.getClass().getSuperclass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException ignored) {
            try {
                field = object.getClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                throw e;
            }
        }
        field.setAccessible(true);
        return field.get(object);
    }

    public static String formatDate(Date date, String format, String timeZone) {
        if (date == null) {
            return "";
        }

        if (StringUtils.isBlank(format)) {
            format = "dd/MM/yyyy hh:mm a";
        }

        SimpleDateFormat dateFormat;
        try {
            dateFormat = new SimpleDateFormat(format);
        } catch (IllegalArgumentException e) {
            dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        }

        if (StringUtils.isNotBlank(timeZone)) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        } else {
            dateFormat.setTimeZone(TimeZone.getTimeZone(GlobalConstants.DEFAULT_TIMEZONE));
        }

        return dateFormat.format(date);
    }

    public static String formatDecimal(BigDecimal value) {
        DecimalFormat df = new DecimalFormat();
        df.setDecimalSeparatorAlwaysShown(true);
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        return value != null ? df.format(value) : "0.00";
    }

    public static String formatDecimal(BigDecimal value, int decimals) {
        DecimalFormat df = new DecimalFormat();
        df.setDecimalSeparatorAlwaysShown(decimals > 0);
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMaximumFractionDigits(decimals);
        df.setMinimumFractionDigits(decimals);

        return value != null ? df.format(value) : "0.00";
    }

    public static String generateFileName(String name, String extension) {
        return name + System.currentTimeMillis() + "." + extension;
    }

}
