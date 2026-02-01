package com.assessment.bookstore.tests;

import com.assessment.bookstore.allure.AllureEnvironment;
import com.assessment.bookstore.client.AuthorsClient;
import com.assessment.bookstore.client.BooksClient;
import org.junit.jupiter.api.BeforeAll;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class BaseApiTest {

    protected final BooksClient booksClient = new BooksClient();
    protected final AuthorsClient authorsClient = new AuthorsClient();

    private static boolean allureEnvWritten = false;

    protected static int uniqueId() {
        return (int) (System.currentTimeMillis() % 1_000_000_000L);
    }

    @BeforeAll
    static void beforeAll() {
        if (!allureEnvWritten) {
            Path dir = Paths.get(
                    System.getProperty("user.dir"),
                    "build",
                    "allure-results"
            );
            AllureEnvironment.write(dir);
            allureEnvWritten = true;
        }
    }
}
