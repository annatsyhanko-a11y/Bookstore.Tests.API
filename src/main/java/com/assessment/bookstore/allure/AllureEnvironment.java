package com.assessment.bookstore.allure;

import com.assessment.bookstore.config.TestConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class AllureEnvironment {

    private AllureEnvironment() {}

    public static void write(Path allureResultsDir) {
        Properties props = new Properties();

        props.setProperty("Environment", TestConfig.env());
        props.setProperty("Base URL", TestConfig.baseUrl());
        props.setProperty("API Prefix", TestConfig.apiPrefix());

        try {
            Files.createDirectories(allureResultsDir);
            try (OutputStream os = Files.newOutputStream(
                    allureResultsDir.resolve("environment.properties"))) {
                props.store(os, "Allure Environment");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write Allure environment", e);
        }
    }
}
