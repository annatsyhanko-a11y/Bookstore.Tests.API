package com.assessment.bookstore.util;

import java.time.Instant;
import java.time.LocalDate;

public class DateUtils {

    private DateUtils() {
    }

    public static String nowIso() {
        return Instant.now().toString();
    }

    public static String today() {
        return LocalDate.now().toString();
    }
}
