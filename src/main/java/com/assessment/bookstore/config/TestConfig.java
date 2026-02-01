package com.assessment.bookstore.config;

import java.io.InputStream;
import java.util.Properties;

public final class TestConfig {

    private static final String DEFAULT_ENV = "qa";
    private static final int DEFAULT_CONNECT_TIMEOUT_MS = 5000;
    private static final int DEFAULT_SOCKET_TIMEOUT_MS = 10000;

    private static final String ENV = read("ENV", DEFAULT_ENV);
    private static final Properties PROPS = loadProps("env/" + ENV + ".properties");

    private TestConfig() {}

    public static String env() {
        return ENV;
    }

    public static String baseUrl() {
        return overrideOrRead("BASE_URL", "base_url");
    }

    public static String apiPrefix() {
        return normalizePath(overrideOrRead("API_PREFIX", "api.prefix"));
    }

    public static int connectTimeoutMs() {
        return readOrDefaultInt("HTTP_CONNECT_TIMEOUT_MS", "http.timeout.connect.ms", DEFAULT_CONNECT_TIMEOUT_MS);
    }

    public static int socketTimeoutMs() {
        return readOrDefaultInt("HTTP_SOCKET_TIMEOUT_MS", "http.timeout.socket.ms", DEFAULT_SOCKET_TIMEOUT_MS);
    }

    private static Properties loadProps(String resourcePath) {
        Properties p = new Properties();
        try (InputStream is = TestConfig.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalStateException("Config not found: " + resourcePath +
                        " (set ENV=dev|qa|stage|prod or create the file)");
            }
            p.load(is);
            return p;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config: " + resourcePath, e);
        }
    }

    private static String read(String key, String defaultValue) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys;

        String env = System.getenv(key);
        if (env != null && !env.isBlank()) return env;

        return defaultValue;
    }

    private static String overrideOrRead(String overrideKey, String fileKey) {
        String value = System.getProperty(overrideKey);
        if (value != null && !value.isBlank()){
            return value;
        }

        value = System.getenv(overrideKey);
        if (value != null && !value.isBlank()){
            return value;
        }

        value = PROPS.getProperty(fileKey);
        if (value != null && !value.isBlank()) {
            return value;
        }

        throw new IllegalStateException("Missing required configuration: " + overrideKey + " (or property '" + fileKey + "')");
    }

    private static String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalStateException("API prefix is blank");
        }
        String p = path.trim();
        return p.startsWith("/") ? p : "/" + p;
    }

    private static int readOrDefaultInt(String overrideKey, String fileKey, int defaultValue) {
        String value = System.getProperty(overrideKey);
        if (value == null || value.isBlank()){
            value = System.getenv(overrideKey);
        }
        if (value == null || value.isBlank()){
            value = PROPS.getProperty(fileKey);
        }
        if (value == null || value.isBlank()){
            return defaultValue;
        }
        try {
            int intValue = Integer.parseInt(value.trim());
            if (intValue <= 0) {
                throw new IllegalStateException(
                        "Timeout must be > 0 for " + overrideKey + " / '" + fileKey + "', but was: " + intValue
                );
            }
            return intValue;
        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                    "Invalid integer value for " + overrideKey + " / '" + fileKey + "': '" + value + "'", e);
        }
    }
}
