package com.assessment.bookstore.spec

import com.assessment.bookstore.allure.AllureEnvironment
import com.assessment.bookstore.client.AuthorsClient
import com.assessment.bookstore.client.BooksClient
import spock.lang.Specification

import java.nio.file.Paths

abstract class BaseApiSpec extends Specification {

    protected final BooksClient booksClient = new BooksClient()
    protected final AuthorsClient authorsClient = new AuthorsClient()

    private static boolean allureEnvWritten = false

    def setupSpec() {
        if (!allureEnvWritten) {
            def dir = Paths.get(System.getProperty("user.dir"), "build", "allure-results")
            AllureEnvironment.write(dir)
            allureEnvWritten = true
        }
    }
}
