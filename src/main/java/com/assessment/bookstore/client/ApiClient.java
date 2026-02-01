package com.assessment.bookstore.client;

import com.assessment.bookstore.config.TestConfig;
import com.github.dzieciou.testing.curl.CurlRestAssuredConfigFactory;
import com.github.dzieciou.testing.curl.Options;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.slf4j.event.Level;

public final class ApiClient {

    private static final Options CURL_OPTIONS = Options.builder()
            .useLogLevel(Level.DEBUG)
            .printMultiliner()
            .build();

    private static final RestAssuredConfig BASE_CONFIG =
            RestAssuredConfig.config().httpClient(
                    HttpClientConfig.httpClientConfig()
                            .setParam("http.connection.timeout", TestConfig.connectTimeoutMs())
                            .setParam("http.socket.timeout", TestConfig.socketTimeoutMs())
            );

    private static final RestAssuredConfig CURL_CONFIG =
            CurlRestAssuredConfigFactory.updateConfig(BASE_CONFIG, CURL_OPTIONS);

    private static final RequestSpecification BASE_SPEC =
            new RequestSpecBuilder()
                    .setBaseUri(TestConfig.baseUrl())
                    .setBasePath(TestConfig.apiPrefix())
                    .setContentType(ContentType.JSON)
                    .setAccept(ContentType.JSON)
                    .setConfig(CURL_CONFIG)
                    .addFilter(new AllureRestAssured())
                    .build();

    private ApiClient() {}

    public static RequestSpecification givenApi() {
        return RestAssured.given()
                .spec(BASE_SPEC)
                .log().ifValidationFails();
    }
}
