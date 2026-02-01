package com.assessment.bookstore.tests;

import com.assessment.bookstore.data.schema.SchemaPaths;
import com.assessment.bookstore.model.Book;
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

import static com.assessment.bookstore.assertions.ApiAssertions.*;
import static com.assessment.bookstore.data.TestData.book;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("Books API")
class BooksApiTest extends BaseApiTest {

    private static final int EXISTING_ID = 1;

    @Smoke
    @Test
    @org.junit.jupiter.api.DisplayName("GET /Books returns 200, JSON, non-empty list and items have required fields")
    void getBooksReturnsOkJsonNonEmptyListWithRequiredFields() {
        Response response = booksClient.getAll();

       assertOkJson(response);

        List<Map<String, Object>> list = response.jsonPath().getList("$");
        assertThat(list).isNotNull().isNotEmpty();

        assertThat(list).allSatisfy(item -> {
            assertThat(item.get("id")).as("id").isNotNull();
            assertThat(item.get("title")).as("title").isNotNull();
        });

        response.then().body(matchesJsonSchemaInClasspath(SchemaPaths.BOOKS_LIST));
    }

    @Smoke
    @Test
    @DisplayName("GET /Books/{id} for existing id returns 200, JSON and correct id/title")
    void getBookByIdWhenExistsReturnsOkJsonAndCorrectIdTitle() {
        Response response = booksClient.getById(EXISTING_ID);

        assertOkJson(response);

        int id = response.jsonPath().getInt("id");
        String title = response.jsonPath().getString("title");

        assertThat(id).isEqualTo(EXISTING_ID);
        assertThat(title).isNotNull();
    }

    @Smoke
    @Test
    @DisplayName("GET /Books/{id} response matches book schema")
    void getBookByIdResponseMatchesBookSchema() {
        Response response = booksClient.getById(EXISTING_ID);

        assertOkJson(response);
        response.then().body(matchesJsonSchemaInClasspath(SchemaPaths.BOOK));
    }

    @ParameterizedTest(name = "GET /Books/{0} for non-existing id returns 404")
    @ValueSource(ints = {0, -1, 999999})
    @DisplayName("GET /Books/{id} for non-existing id returns 404")
    void getBookByIdWhenNotExistsReturnsNotFound(int id) {
        Response response = booksClient.getById(id);

        assertStatus(response, HttpStatus.SC_NOT_FOUND);
    }

    @Smoke
    @Test
    @DisplayName("POST /Books creates a new book (200)")
    void postBooksCreatesNewBookReturnsOk() {
        int id = uniqueId();
        Book book = book(id);

        Response created = booksClient.create(book);

        assertOkJson(created);
        created.then().body(matchesJsonSchemaInClasspath(SchemaPaths.BOOK));

        int createdId = created.jsonPath().getInt("id");
        String createdTitle = created.jsonPath().getString("title");

        assertThat(createdId).isEqualTo(id);
        assertThat(createdTitle).isEqualTo(book.getTitle());
    }

    @Test
    @DisplayName("PUT /Books/{id} updates book (200)")
    void putBookByIdUpdatesBookReturnsOk() {
        int id = EXISTING_ID;
        Book payload = book(id).toBuilder()
                .title("Updated " + UUID.randomUUID())
                .build();

        Response updated = booksClient.update(id, payload);

        assertOkJson(updated);
        updated.then().body(matchesJsonSchemaInClasspath(SchemaPaths.BOOK));

        int updatedId = updated.jsonPath().getInt("id");
        String updatedTitle = updated.jsonPath().getString("title");

        assertThat(updatedId).isEqualTo(id);
        assertThat(updatedTitle).isEqualTo(payload.getTitle());
    }

    @Issue("Test-Issue-3")
    @ParameterizedTest(name = "PUT /Books/{0} for non-existing id returns 404")
    @ValueSource(ints = {0, -1, 999999})
    @DisplayName("PUT /Books/{id} for non-existing id returns 404")
    void putBookByIdWhenNotExistsReturnsNotFound(int id) {
        Book payload = book(id);

        Response response = booksClient.update(id, payload);

        assertStatus(response, HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("DELETE /Books/{id} deletes existing book and makes it unavailable")
    void deleteBookByIdDeletesBookAndMakesItUnavailable() {
        int id = uniqueId();
        Book payload = book(id);

        Response created = booksClient.create(payload);
        assertOkJson(created);

        int createdId = created.jsonPath().getInt("id");
        assertThat(createdId).isEqualTo(id);

        Response deleted = booksClient.delete(id);
        assertStatus(deleted, HttpStatus.SC_OK);

        Response getAfterDelete = booksClient.getById(id);
        assertStatus(getAfterDelete, HttpStatus.SC_NOT_FOUND);
    }

    @Issue("Test-Issue-4")
    @ParameterizedTest(name = "DELETE /Books/{0} for non-existing id returns 404")
    @ValueSource(ints = {0, -1, 999999})
    @DisplayName("DELETE /Books/{id} for non-existing id returns 404")
    void deleteBookByIdWhenNotExistsReturnsNotFound(int id) {
        Response response = booksClient.delete(id);

        assertStatus(response, HttpStatus.SC_NOT_FOUND);
    }
}

