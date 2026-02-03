package com.assessment.bookstore.tests;

import com.assessment.bookstore.model.Author;
import com.assessment.bookstore.data.schema.SchemaPaths;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.assessment.bookstore.tags.Smoke;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.assessment.bookstore.assertions.ApiAssertions.assertOkJson;
import static com.assessment.bookstore.assertions.ApiAssertions.assertStatus;
import static com.assessment.bookstore.data.TestData.author;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("Authors API")
class AuthorsApiTest extends BaseApiTest {

    private static final int EXISTING_ID = 1;

    @Smoke
    @Test
    @DisplayName("GET /Authors returns 200, JSON, non-empty list and each item has required fields")
    void getAuthorsReturnsOkJsonNonEmptyListWithRequiredFields() {
        Response response = authorsClient.getAll();

        assertOkJson(response);

        List<Map<String, Object>> list = response.jsonPath().getList("$");
        assertThat(list).isNotNull().isNotEmpty();

        assertThat(list).allSatisfy(item -> {
            assertThat(item.get("id")).as("id").isNotNull();
            assertThat(item.get("firstName")).as("firstName").isNotNull();
        });

        response.then().body(matchesJsonSchemaInClasspath(SchemaPaths.AUTHORS_LIST));
    }

    @Test
    @Smoke
    @DisplayName("GET /Authors/{id} returns 200, JSON and matches schema")
    void getAuthorByIdReturnsOkJsonAndMatchesSchema() {
        Response response = authorsClient.getById(EXISTING_ID);

        assertOkJson(response);
        response.then().body(matchesJsonSchemaInClasspath(SchemaPaths.AUTHOR));

        int id = response.jsonPath().getInt("id");
        String firstName = response.jsonPath().getString("firstName");

        assertThat(id).isEqualTo(EXISTING_ID);
        assertThat(firstName).isNotNull();
    }

    @Test
    @Smoke
    @DisplayName("GET /Authors/{id} for existing id returns 200 and correct payload fields")
    void getAuthorByIdWhenExistsReturnsOkAndPayloadHasFields() {
        Response response = authorsClient.getById(EXISTING_ID);

        assertOkJson(response);

        int id = response.jsonPath().getInt("id");
        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");

        assertThat(id).isEqualTo(EXISTING_ID);
        assertThat(firstName).isNotNull();
        assertThat(lastName).isNotNull();
    }

    @ParameterizedTest(name = "GET /Authors/{0} returns 404 for non-existing id")
    @ValueSource(ints = {0, -1, 999999})
    @DisplayName("GET /Authors/{id} returns 404 for non-existing id")
    void getAuthorByIdWhenNotExistsReturnsNotFound(int id) {
        Response response = authorsClient.getById(id);

        assertStatus(response, HttpStatus.SC_NOT_FOUND);
    }

    @Smoke
    @Test
    @DisplayName("POST /Authors creates author (200), response matches schema")
    void postAuthorsCreatesAuthorReturnsOkJsonAndMatchesSchema() {
        int id = uniqueId();
        Author payload = author(id, 1);

        Response created = authorsClient.create(payload);

        assertOkJson(created);
        created.then().body(matchesJsonSchemaInClasspath(SchemaPaths.AUTHOR));

        int createdId = created.jsonPath().getInt("id");
        String createdFirstName = created.jsonPath().getString("firstName");

        assertThat(createdId).isEqualTo(id);
        assertThat(createdFirstName).isEqualTo(payload.getFirstName());
    }

    @Test
    @Issue("Test-Issue-1")
    @DisplayName("POST /Authors with invalid payload returns 400")
    void postAuthorsWithInvalidPayloadReturnsBadRequest() {
        Author invalid = Author.builder()
                .id(uniqueId())
                .idBook(1)
                .firstName("")
                .lastName("X")
                .build();

        Response response = authorsClient.create(invalid);

        assertStatus(response, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("PUT /Authors/{id} updates author and returns 200")
    void putAuthorByIdUpdatesAuthorReturnsOk() {
        int id = 1;
        Author payload = author(id, 1).toBuilder()
                .lastName("Updated-" + UUID.randomUUID())
                .build();

        Response response = authorsClient.update(id, payload);

        assertStatus(response, HttpStatus.SC_OK);

        int returnedId = response.jsonPath().getInt("id");
        String returnedLastName = response.jsonPath().getString("lastName");

        assertThat(returnedId).isEqualTo(id);
        assertThat(returnedLastName).isEqualTo(payload.getLastName());
    }

    @Test
    @Issue("Test-Issue-2")
    @DisplayName("PUT /Authors/{id} for non-existing id returns 404")
    void putAuthorByIdWhenNotExistsReturnsNotFound() {
        int id = 999999;
        Author payload = author(id, 1);

        Response response = authorsClient.update(id, payload);

        assertStatus(response, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("DELETE /Authors/{id} removes author and subsequent GET returns 404")
    void deleteAuthorByIdRemovesAuthorAndGetReturnsNotFound() {
        int id = uniqueId();
        Author payload = author(id, 1);

        Response created = authorsClient.create(payload);
        assertOkJson(created);

        int createdId = created.jsonPath().getInt("id");
        assertThat(createdId).isEqualTo(id);

        Response deleted = authorsClient.delete(id);
        assertStatus(deleted, HttpStatus.SC_OK);

        Response getAfterDelete = authorsClient.getById(id);
        assertStatus(getAfterDelete, HttpStatus.SC_NOT_FOUND);
    }
}
