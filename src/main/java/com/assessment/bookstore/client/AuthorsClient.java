package com.assessment.bookstore.client;

import io.restassured.response.Response;
import com.assessment.bookstore.model.Author;

public class AuthorsClient {

    private static final String AUTHORS = "/Authors";
    private static final String AUTHOR_BY_ID = AUTHORS + "/{id}";
    private static final String AUTHORS_BY_BOOK = AUTHORS + "/authors/books/{idBook}";


    public Response getAll() {
        return ApiClient.givenApi()
                .when()
                .get(AUTHORS);
    }

    public Response getById(int id) {
        return ApiClient.givenApi()
                .pathParam("id", id)
                .when()
                .get(AUTHOR_BY_ID);
    }

    public Response getByBookId(int idBook) {
        return ApiClient.givenApi()
                .pathParam("idBook", idBook)
                .when()
                .get(AUTHORS_BY_BOOK);
    }

    public Response create(Author author) {
        return ApiClient.givenApi()
                .body(author)
                .when()
                .post(AUTHORS);
    }

    public Response update(int id, Author author) {
        return ApiClient.givenApi()
                .pathParam("id", id)
                .body(author)
                .when()
                .put(AUTHOR_BY_ID);
    }

    public Response delete(int id) {
        return ApiClient.givenApi()
                .pathParam("id", id)
                .when()
                .delete(AUTHOR_BY_ID);
    }
}