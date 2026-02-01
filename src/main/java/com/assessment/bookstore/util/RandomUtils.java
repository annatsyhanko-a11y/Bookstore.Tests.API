package com.assessment.bookstore.util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
    private RandomUtils() {
    }

    public static String randomString(int length) {
        return UUID.randomUUID().toString().replace("-", "")
                .substring(0, length);
    }

    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }
}
