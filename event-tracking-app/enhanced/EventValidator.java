package com.example.cs360projectonelynneamarihugh;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EventValidator {

    public static boolean isValidTitle(String title) {
        return title != null && !title.trim().isEmpty();
    }

    public static boolean isValidDate(String date) {
        if (date == null || !date.trim().matches("\\d{2}/\\d{2}/\\d{4}")) {
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        format.setLenient(false);
        try {
            format.parse(date.trim());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isValidTime(String time) {
        if (time == null || time.trim().isEmpty()) {
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat("h:mm a", Locale.US);
        format.setLenient(false);
        try {
            format.parse(time.trim());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}