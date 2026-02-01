package com.assessment.bookstore.client;

import io.restassured.response.Response;
import com.assessment.bookstore.model.Book;


public class BooksClient {

    private static final String BOOKS = "/Books";
    private static final String BOOK_BY_ID = "/Books/{id}";


    public Response getAll() {
        return ApiClient.givenApi()
                .when()
                .get(BOOKS);
    }

    public Response getById(int id) {
        return ApiClient.givenApi()
                .pathParam("id", id)
                .when()
                .get(BOOK_BY_ID);
    }

    public Response create(Book book) {
        return ApiClient.givenApi()
                .body(book)
                .when()
                .post(BOOKS);
    }

    public Response update(int id, Book book) {
        return ApiClient.givenApi()
                .pathParam("id", id)
                .body(book)
                .when()
                .put(BOOK_BY_ID);
    }

    public Response delete(int id) {
        return ApiClient.givenApi()
                .pathParam("id", id)
                .when()
                .delete(BOOK_BY_ID);
    }
}
