package com.assessment.bookstore.data

import com.assessment.bookstore.model.Author
import com.assessment.bookstore.model.Book

import java.time.OffsetDateTime

final class TestData {

    static Book book(Integer id = null) {
        Book.builder()
                .id(id)
                .title("Title " + UUID.randomUUID())
                .description("Description")
                .pageCount(123)
                .excerpt("Excerpt")
        // safest for demo API: string datetime
                .publishDate(OffsetDateTime.now().toString())
                .build()
    }

    static Author author(Integer id = null, Integer idBook = 1) {
        Author.builder()
                .id(id)
                .idBook(idBook)
                .firstName("John")
                .lastName("Doe")
                .build()
    }

    private TestData() {}
}
