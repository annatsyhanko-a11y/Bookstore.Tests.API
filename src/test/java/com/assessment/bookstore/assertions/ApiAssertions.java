package com.assessment.bookstore.assertions;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public final class ApiAssertions {
    private ApiAssertions() {
    }

    public static void assertStatus(Response response, int expectedStatus) {
        assertThat(response.getStatusCode())
                .as("HTTP status code")
                .isEqualTo(expectedStatus);
    }

    public static void assertJson(Response response) {
        assertThat(response.getHeader("Content-Type"))
                .as("Content-Type header")
                .isNotNull()
                .contains("application/json");
    }

    public static void assertOkJson(Response response) {
        assertStatus(response, HttpStatus.SC_OK);
        assertJson(response);
    }
}
