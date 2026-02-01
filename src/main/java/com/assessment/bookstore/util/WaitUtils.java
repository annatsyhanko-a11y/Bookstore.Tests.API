package com.assessment.bookstore.util;

import java.time.Duration;
import java.util.function.Supplier;

public final class WaitUtils {

    private static final long POLL_INTERVAL_MS = 300;

    private WaitUtils() {}

    public static void waitUntil(Supplier<Boolean> condition, Duration timeout) {
        long end = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < end) {
            if (condition.get()) {
                return;
            }
            sleep(POLL_INTERVAL_MS);
        }

        throw new AssertionError(
                "Condition not met within " + timeout.toMillis() + " ms"
        );
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting", e);
        }
    }
}
