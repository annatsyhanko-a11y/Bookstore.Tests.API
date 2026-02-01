package com.assessment.bookstore.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "classpath:env/${env}.properties",
        "classpath:env/qa.properties"
})
public interface AppConfig extends Config {

    @Key("BASE_URL")
    String baseUrl();

    @Key("API_PREFIX")
    @DefaultValue("/api")
    String apiPrefix();

    @Key("HTTP_CONNECT_TIMEOUT_MS")
    @DefaultValue("5000")
    int connectTimeoutMs();

    @Key("HTTP_SOCKET_TIMEOUT_MS")
    @DefaultValue("10000")
    int socketTimeoutMs();

    static AppConfig load() {
        String env = System.getProperty("ENV");
        if (env == null || env.isBlank()) {
            env = System.getenv("ENV");
        }
        if (env == null || env.isBlank()) {
            env = "qa";
        }

        ConfigFactory.setProperty("env", env);

        return ConfigFactory.create(AppConfig.class, System.getProperties());
    }
}
