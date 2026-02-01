package com.assessment.bookstore.spec

import com.assessment.bookstore.data.TestData
import com.assessment.bookstore.data.schema.SchemaPaths
import com.assessment.bookstore.model.Book
import io.qameta.allure.Feature
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.lang.Tag
import spock.lang.Unroll

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath

@Feature("Books API")
class BooksApiSpec extends BaseApiSpec {

    @Tag('smoke')
    def "GET /Books returns 200 and a non-empty list"() {
        when:
        Response response = booksClient.getAll()

        then:
        response.statusCode() == HttpStatus.SC_OK
        response.jsonPath().getList('$').size() > 0
    }

    @Tag('smoke')
    def "GET /Books/{id} for existing id returns 200 and correct id"() {
        when:
        Response response = booksClient.getById(1)

        then:
        response.statusCode() == HttpStatus.SC_OK
        response.body().path("id") == 1
        response.body().path("title") != null
    }

    @Tag('smoke')
    def "GET /Books/{id} response matches book schema"() {
        when:
        def response = booksClient.getById(1)

        then:
        response.statusCode() == HttpStatus.SC_OK
        response.then().body(matchesJsonSchemaInClasspath(SchemaPaths.BOOK))
    }

    @Unroll
    def "GET /Books/{id} for non-existing id=#id returns 404"() {
        when:
        Response response = booksClient.getById(id)

        then:
        response.statusCode() == HttpStatus.SC_NOT_FOUND

        where:
        id << [0, -1, 999999]
    }

    @Tag('smoke')
    def "POST /Books creates a new book successfully"() {
        given:
        int id = 100000 + new Random().nextInt(900000)
        Book book = TestData.book(id)

        when:
        Response response = booksClient.create(book)

        then:
        response.statusCode() == HttpStatus.SC_OK
        response.body().path("id") == id
        response.body().path("title") == book.title
    }

    def "PUT /Books/{id} updates book and returns 200"() {
        given:
        int id = 1
        Book payload = TestData.book(id).toBuilder()
                .title("Updated " + UUID.randomUUID())
                .build()

        when:
        Response response = booksClient.update(id, payload)

        then:
        response.statusCode() == HttpStatus.SC_OK
        response.body().path("id") == id
        response.body().path("title") == payload.title
    }

    @Unroll
    def "PUT /Books/{id} for non-existing id=#id returns 404"() {
        given:
        Book payload = TestData.book(id)

        when:
        Response response = booksClient.update(id, payload)

        then:
        response.statusCode() == HttpStatus.SC_NOT_FOUND

        where:
        id << [0, -1, 999999]
    }

    def "DELETE /Books/{id} deletes existing book and makes it unavailable"() {
        given: "an existing book"
        int id = 100000 + new Random().nextInt(900000)
        Book payload = TestData.book(id)

        def created = booksClient.create(payload)
        created.statusCode() == HttpStatus.SC_OK

        when: "the book is deleted"
        def deleted = booksClient.delete(id)

        then: "delete operation succeeds"
        deleted.statusCode() == HttpStatus.SC_OK

        when: "the book is requested again"
        def getAfterDelete = booksClient.getById(id)

        then: "the book is no longer available"
        getAfterDelete.statusCode() == HttpStatus.SC_NOT_FOUND
    }

    @Unroll
    def "DELETE /Books/{id} for non-existing id=#id returns 404"() {
        when:
        Response response = booksClient.delete(id)

        then:
        response.statusCode() == HttpStatus.SC_NOT_FOUND

        where:
        id << [0, -1, 999999]
    }
}
