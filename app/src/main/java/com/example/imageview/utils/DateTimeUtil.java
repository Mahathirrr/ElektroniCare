package com.example.imageview.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class DateTimeUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd MMMM yyyy, HH:mm", new Locale("id", "ID"));
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", new Locale("id", "ID"));

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date);
    }

    public static String formatDateTime(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_TIME_FORMAT.format(date);
    }

    public static String formatTime(Date date) {
        if (date == null) {
            return "";
        }
        return TIME_FORMAT.format(date);
    }

    public static String getTimeAgo(Date date) {
        if (date == null) {
            return "";
        }

        long time = date.getTime();
        long now = System.currentTimeMillis();
        long diff = now - time;

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + " hari yang lalu";
        } else if (hours > 0) {
            return hours + " jam yang lalu";
        } else if (minutes > 0) {
            return minutes + " menit yang lalu";
        } else {
            return "Baru saja";
        }
    }

    public static String formatEstimatedDays(int days) {
        if (days == 0) {
            return "Hari ini";
        } else if (days == 1) {
            return "1 hari";
        } else {
            return days + " hari";
        }
    }
}
