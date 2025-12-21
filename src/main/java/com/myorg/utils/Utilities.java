package com.myorg.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.myorg.utils.GlobalConstants.UPLOADS_TEMP_FILES_PATH;

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

        return value != null ? df.format(value) : df.format(BigDecimal.ZERO);
    }

    public static String generateFileName(String name, String extension) {
        return name + System.currentTimeMillis() + "." + extension;
    }

    public static List<String> listBooleanYesNo() {
        return List.of("Yes", "No");
    }

    public static boolean isValidUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            URI uri = url.toURI();
            // Optional: add further checks for specific schemes, hosts, etc. if needed
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    private static Path getWorkingDir() {
        return Path.of(System.getProperty("user.dir") + UPLOADS_TEMP_FILES_PATH);
    }

    private static Path getWorkingDir(String fileName) {
        return Path.of(System.getProperty("user.dir") + UPLOADS_TEMP_FILES_PATH + "/" + fileName);
    }

    public static Path byteArrayToPath(byte[] data) {

        String fileName = "tmp" + System.currentTimeMillis() + ".csv";
        Path projectDir = getWorkingDir(fileName);
        try {
            // Write the byte array to the file at the specified Path
            return Files.write(projectDir, data);
        } catch (IOException e) {
            System.err.println("An error occurred while writing the file: " + e.getMessage());
        }

        return null;
    }

    public static boolean deleteTempFile(String filename) {
        try {
            Path file = getWorkingDir()
                    .resolve(filename); // Resolve the file's path
            return Files.deleteIfExists(file); // Deletes the file if it exists
        } catch (IOException e) {
            // Handle the exception appropriately (e.g., log the error, throw a custom exception)
            return false;
        }
    }
}
