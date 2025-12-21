package com.myorg.utils;

import java.util.LinkedList;
import java.util.List;

public class GlobalConstants {
    public static String LOGO                    = "images/logo.jpg";
    public static String DEFAULT_TIMEZONE        = "America/Santo_Domingo";
    public static int    MAX_FILE_SIZE_IN_BYTES  = 25 * 1024 * 1024; // 10MB
    public static String UPLOADS_TEMP_FILES_PATH = "/uploads/tmpFiles";

    public static LinkedList<String> COLOR_LIST = new LinkedList<>(
            List.of("#ccffcc", "#ffe6f7", "#e6fff2", "#ffffe6", "#f9ecf2", "#e0ebeb",
                    "#ebebe0", "#f2e6ff", "#ccebff"));

}
