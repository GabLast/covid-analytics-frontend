package com.myorg.utils;

import com.myorg.encapsulations.UserSetting;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtilities {

    public static LocalDate getLocalDateAtTimeZoneAtStartOrEnd(String timeZoneId, LocalDate date, boolean startOfDay) {
        if (date == null) {
            return null;
        }

        if(StringUtils.isBlank(timeZoneId)) {
            timeZoneId = GlobalConstants.DEFAULT_TIMEZONE;
        }

        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        if (timeZone == null) {
            timeZone = TimeZone.getTimeZone(GlobalConstants.DEFAULT_TIMEZONE);
        }

        return startOfDay ?
                date.atStartOfDay().atZone(timeZone.toZoneId()).toLocalDate() :
                date.atTime(LocalTime.MAX).atZone(timeZone.toZoneId()).toLocalDate();
    }

    public static LocalDate getLocalDateAtTimeZoneAtStartOrEnd(String timeZoneId, LocalDateTime date, boolean startOfDay) {
        if (date == null) {
            return null;
        }

        if(StringUtils.isBlank(timeZoneId)) {
            timeZoneId = GlobalConstants.DEFAULT_TIMEZONE;
        }

        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        if (timeZone == null) {
            timeZone = TimeZone.getTimeZone(GlobalConstants.DEFAULT_TIMEZONE);
        }

        return startOfDay ?
                date.toLocalDate().atStartOfDay().atZone(timeZone.toZoneId()).toLocalDate() :
                date.toLocalDate().atTime(LocalTime.MAX).atZone(timeZone.toZoneId()).toLocalDate();
    }

    public static LocalDateTime getLocalDateTimeAtTimeZoneAtStartOrEnd(String timeZoneId, LocalDate date, boolean startOfDay) {
        if (date == null) {
            return null;
        }

        if(StringUtils.isBlank(timeZoneId)) {
            timeZoneId = GlobalConstants.DEFAULT_TIMEZONE;
        }

        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        if (timeZone == null) {
            timeZone = TimeZone.getTimeZone(GlobalConstants.DEFAULT_TIMEZONE);
        }

        return startOfDay ?
                date.atStartOfDay().atZone(timeZone.toZoneId()).toLocalDateTime() :
                date.atTime(LocalTime.MAX).atZone(timeZone.toZoneId()).toLocalDateTime();
    }

    public static LocalDateTime getLocalDateTimeAtTimeZoneAtStartOrEnd(String timeZoneId, LocalDateTime date, boolean startOfDay) {
        if (date == null) {
            return null;
        }

        if(StringUtils.isBlank(timeZoneId)) {
            timeZoneId = GlobalConstants.DEFAULT_TIMEZONE;
        }

        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        if (timeZone == null) {
            timeZone = TimeZone.getTimeZone(GlobalConstants.DEFAULT_TIMEZONE);
        }

        return startOfDay ?
                date.toLocalDate().atStartOfDay().atZone(timeZone.toZoneId()).toLocalDateTime() :
                date.toLocalDate().atTime(LocalTime.MAX).atZone(timeZone.toZoneId()).toLocalDateTime();
    }

    public static LocalDate getLocalDateAtTimeZoneAtStartOrEnd(TimeZone timeZone, LocalDate date, boolean startOfDay) {
        if (date == null) {
            return null;
        }

        if (timeZone == null) {
            timeZone = TimeZone.getTimeZone(GlobalConstants.DEFAULT_TIMEZONE);
        }

        return startOfDay ?
                date.atStartOfDay().atZone(timeZone.toZoneId()).toLocalDate() :
                date.atTime(LocalTime.MAX).atZone(timeZone.toZoneId()).toLocalDate();
    }

    public static LocalDate getLocalDateAtTimeZoneAtStartOrEnd(TimeZone timeZone, LocalDateTime date, boolean startOfDay) {
        if (date == null) {
            return null;
        }

        if (timeZone == null) {
            timeZone = TimeZone.getTimeZone(GlobalConstants.DEFAULT_TIMEZONE);
        }

        return startOfDay ?
                date.toLocalDate().atStartOfDay().atZone(timeZone.toZoneId()).toLocalDate() :
                date.toLocalDate().atTime(LocalTime.MAX).atZone(timeZone.toZoneId()).toLocalDate();
    }

    public static LocalDateTime getLocalDateTimeAtTimeZoneAtStartOrEnd(TimeZone timeZone, LocalDate date, boolean startOfDay) {
        if (date == null) {
            return null;
        }

        if (timeZone == null) {
            timeZone = TimeZone.getTimeZone(GlobalConstants.DEFAULT_TIMEZONE);
        }

        return startOfDay ?
                date.atStartOfDay().atZone(timeZone.toZoneId()).toLocalDateTime() :
                date.atTime(LocalTime.MAX).atZone(timeZone.toZoneId()).toLocalDateTime();
    }

    public static LocalDateTime getLocalDateTimeAtTimeZoneAtStartOrEnd(TimeZone timeZone, LocalDateTime date, boolean startOfDay) {
        if (date == null) {
            return null;
        }

        if (timeZone == null) {
            timeZone = TimeZone.getTimeZone(GlobalConstants.DEFAULT_TIMEZONE);
        }

        return startOfDay ?
                date.toLocalDate().atStartOfDay().atZone(timeZone.toZoneId()).toLocalDateTime() :
                date.toLocalDate().atTime(LocalTime.MAX).atZone(timeZone.toZoneId()).toLocalDateTime();
    }

    public static LocalDateTime getLocalDateTimeAtTimeZone(String timeZoneId, LocalDateTime date) {
        if (date == null) {
            return null;
        }

        if(StringUtils.isBlank(timeZoneId)) {
            timeZoneId = GlobalConstants.DEFAULT_TIMEZONE;
        }

        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        if (timeZone == null) {
            timeZone = TimeZone.getTimeZone(GlobalConstants.DEFAULT_TIMEZONE);
        }

        return date.atZone(timeZone.toZoneId()).toLocalDateTime();
    }

    /**
     * Regular Java Date Type. Try to deprecate their usage.
     */

//                dateStart != null ? Date.from(dateStart.atStartOfDay().atZone(timeZone.toZoneId()).toInstant()) : null,
//                dateEnd != null ? Date.from(dateEnd.atTime(LocalTime.MAX).atZone(timeZone.toZoneId()).toInstant()) : null,
//                Utilities.getTimeAtTimeZoneAtStartOfDay(timeZone, dateStart),
//                Utilities.getTimeAtTimeZoneAtEndOfDay(timeZone, dateEnd),
//                Utilities.getLocalDateTimeAtTimeZoneAtStartOrEnd(null, dateStart, true),
//                Utilities.getLocalDateTimeAtTimeZoneAtStartOrEnd(null, dateEnd, false),

    public static Date getDateAtTimeZoneAtStartOfDay(TimeZone timeZone, Date date) {
        if (date == null) {
            return null;
        }

        if (timeZone == null) {
            timeZone = TimeZone.getTimeZone(GlobalConstants.DEFAULT_TIMEZONE);
        }

        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(date);
        setCalendarToStartOfDay(calendar);
        return calendar.getTime();
    }

    public static Date getDateAtTimeZoneAtEndOfDay(TimeZone timeZone, Date date) {
        if (date == null) {
            return null;
        }

        if (timeZone == null) {
            timeZone = TimeZone.getTimeZone(GlobalConstants.DEFAULT_TIMEZONE);
        }

        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(date);
        setCalendarToEndOfDay(calendar);
        return calendar.getTime();
    }

    public static void setCalendarToStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static void setCalendarToEndOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 59);
    }

    public static String getLocalDateAsString(LocalDate localDate, UserSetting userSetting) {

        if(localDate == null) {
            return "";
        }

        if(userSetting == null) {
            userSetting = UserSetting.builder()
                    .darkMode(false)
                    .language("en")
                    .dateFormat("dd/MM/yyyy")
                    .build();
        }

        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern(userSetting.dateFormat());
        return localDate.format(customFormatter);
    }

    public static LocalDate getLocalDateFromString(String string, UserSetting userSetting) {

        if(string == null) {
            return LocalDate.now();
        }

        if(userSetting == null) {
            userSetting = UserSetting.builder()
                    .darkMode(false)
                    .language("en")
                    .dateFormat("dd/MM/yyyy")
                    .build();
        }

        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern(userSetting.dateFormat());
        return LocalDate.parse(string, customFormatter);
    }

}
