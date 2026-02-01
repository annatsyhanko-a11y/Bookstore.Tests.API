package com.assessment.bookstore.data;

import com.assessment.bookstore.model.Author;
import com.assessment.bookstore.model.Book;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class TestData {

    private TestData() {
    }

    public static Book book(Integer id) {
        return Book.builder()
                .id(id)
                .title("Title " + UUID.randomUUID())
                .description("Description")
                .pageCount(123)
                .excerpt("Excerpt")
                .publishDate(OffsetDateTime.now().toString())
                .build();
    }

    public static Author author(Integer id, Integer idBook) {
        return Author.builder()
                .id(id)
                .idBook(idBook)
                .firstName("John")
                .lastName("Doe")
                .build();
    }
}