package com.taskflowai.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;

public final class DateParseUtil {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    private DateParseUtil() {
    }

    public static LocalDate parseFlexibleDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();

        try {
            return LocalDate.parse(trimmed, ISO);
        } catch (DateTimeParseException ignored) {
            // try other formats below
        }

        String lower = trimmed.toLowerCase();
        LocalDate today = LocalDate.now();

        if (lower.equals("today")) {
            return today;
        }
        if (lower.equals("tomorrow")) {
            return today.plusDays(1);
        }

        for (DayOfWeek day : DayOfWeek.values()) {
            String dayName = day.name().toLowerCase();
            if (lower.contains(dayName)) {
                return today.with(TemporalAdjusters.nextOrSame(day));
            }
        }

        return null;
    }
}
