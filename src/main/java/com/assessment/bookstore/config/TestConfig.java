package com.assessment.bookstore.config;

public final class TestConfig {
    private static final AppConfig CFG = AppConfig.load();

    private TestConfig() {
    }

    public static String env() {
        String env = System.getProperty("env");
        if (env != null && !env.isBlank()) {
            return env;
        }
        env = System.getenv("env");
        if (env != null && !env.isBlank()) {
            return env;
        }
        return "qa";
    }

    public static String baseUrl() {
        return CFG.baseUrl();
    }

    public static String apiPrefix() {
        return normalizePath(CFG.apiPrefix());
    }

    public static int connectTimeoutMs() {
        return CFG.connectTimeoutMs();
    }

    public static int socketTimeoutMs() {
        return CFG.socketTimeoutMs();
    }

    private static String normalizePath(String path) {
        String p = path == null ? "" : path.trim();
        if (p.isBlank()) throw new IllegalStateException("API prefix is blank");
        return p.startsWith("/") ? p : "/" + p;
    }
}
