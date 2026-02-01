package com.assessment.bookstore.client;

import com.assessment.bookstore.config.TestConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class ApiClient {

    private static final RequestSpecification BASE_SPEC =
            new RequestSpecBuilder()
                    .setBaseUri(TestConfig.baseUrl())
                    .setBasePath(TestConfig.apiPrefix())
                    .setContentType(ContentType.JSON)
                    .setAccept(ContentType.JSON)
                    .setConfig(RestAssuredConfig.config().httpClient(
                            HttpClientConfig.httpClientConfig()
                                    .setParam("http.connection.timeout", TestConfig.connectTimeoutMs())
                                    .setParam("http.socket.timeout", TestConfig.socketTimeoutMs())
                    ))
                    .build();

    private ApiClient() {}

    public static RequestSpecification givenApi() {
        return RestAssured.given()
                .spec(BASE_SPEC)
                .filter(new AllureRestAssured())
                .log().ifValidationFails();
    }
}
