package org.example.bankdata;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(PostgreSQLTestResource.class)
class AccountResourceTest {

    @Inject
    AccountRepository accountRepository;

    @Test
    void testCreateAccountEndpointAndValidateDatabase() {
        String requestBody = """
                {
                    "firstName": "John",
                    "lastName": "Doe"
                }
                """;

        Account responseAccount = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/account")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("accountNumber", notNullValue())
                .extract()
                .as(Account.class);

        Account databaseAccount = accountRepository.findById(responseAccount.getId());

        assertEquals(responseAccount.getId(), databaseAccount.getId());
        assertEquals(responseAccount.getFirstName(), databaseAccount.getFirstName());
        assertEquals(responseAccount.getLastName(), databaseAccount.getLastName());
        assertEquals(responseAccount.getAccountNumber(), databaseAccount.getAccountNumber());
        assertEquals(responseAccount.getBalance(), databaseAccount.getBalance());
    }
}
