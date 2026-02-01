package com.assessment.bookstore.spec

import com.assessment.bookstore.data.TestData
import com.assessment.bookstore.data.schema.SchemaPaths
import com.assessment.bookstore.model.Author
import io.qameta.allure.Feature
import io.restassured.response.Response
import org.apache.http.HttpStatus
import spock.lang.Tag
import spock.lang.Unroll
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath

@Feature("Authors API")
class AuthorsApiSpec extends BaseApiSpec {

    @Tag('smoke')
    def "GET /Authors returns 200 and a non-empty list"() {
        when:
        Response response = authorsClient.getAll()

        then:
        response.statusCode() == HttpStatus.SC_OK
        response.jsonPath().getList('$').size() > 0
    }

    @Tag('smoke')
    def "GET /Authors/{id} returns response matching schema"() {
        when:
        def response = authorsClient.getById(1)

        then:
        response.statusCode() == HttpStatus.SC_OK
        response.then()
                .body(matchesJsonSchemaInClasspath(SchemaPaths.AUTHOR))
    }

    @Tag('smoke')
    def "GET /Authors/{id} for existing id returns 200 and correct id"() {
        when:
        Response response = authorsClient.getById(1)

        then:
        response.statusCode() == HttpStatus.SC_OK
        response.body().path("id") == 1
        response.body().path("firstName") != null
    }

    @Unroll
    def "GET /Authors/{id} returns 404 for non-existing id: #id"() {
        when:
        Response response = authorsClient.getById(id)

        then:
        response.statusCode() == HttpStatus.SC_NOT_FOUND

        where:
        id << [0, -1, 999999]
    }

    @Tag('smoke')
    def "POST /Authors creates a new author successfully"() {
        given:
        int id = 100000 + new Random().nextInt(900000)
        Author author = TestData.author(id, 1)

        when:
        Response response = authorsClient.create(author)

        then:
        response.statusCode() == HttpStatus.SC_OK
        response.body().path("id") == id
        response.body().path("firstName") == author.firstName
    }

    def "PUT /Authors/{id} updates author and returns 200"() {
        given:
        int id = 1
        Author payload = TestData.author(id, 1).toBuilder()
                .lastName("Updated-" + UUID.randomUUID())
                .build()

        when:
        Response response = authorsClient.update(id, payload)

        then:
        response.statusCode() == HttpStatus.SC_OK
        response.body().path("id") == id
        response.body().path("lastName") == payload.lastName
    }

    def "DELETE /Authors/{id} returns 200"() {
        given:
        def id = 100000 + new Random().nextInt(900000)
        def payload = TestData.author(id, 1)

        when: "create"
        def created = authorsClient.create(payload)

        then:
        created.statusCode() == HttpStatus.SC_OK

        when: "delete"
        def deleted = authorsClient.delete(id)

        then:
        deleted.statusCode() == HttpStatus.SC_OK

        when: "verify deleted"
        def getAfterDelete = authorsClient.getById(id)

        then:
        getAfterDelete.statusCode() == HttpStatus.SC_NOT_FOUND
    }
}
