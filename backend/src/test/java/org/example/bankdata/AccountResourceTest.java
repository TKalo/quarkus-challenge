package org.example.bankdata;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
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

    @Test
    public void testDepositMoneySuccess() {
        AccountInput input = new AccountInput();
        input.setFirstName("John");
        input.setLastName("Doe");

        Account account = RestAssured.given()
                .contentType("application/json")
                .body(input)
                .when()
                .post("/account")
                .then()
                .statusCode(200)
                .extract()
                .as(Account.class);

        double depositAmount = 100.0;
        RestAssured.given()
                .contentType("application/json")
                .body(depositAmount)
                .when()
                .post("/account/" + account.getAccountNumber() + "/deposit")
                .then()
                .statusCode(200)
                .body("balance", equalTo((float) depositAmount));
    }

    @Test
    public void testDepositNegativeAmount() {
        AccountInput input = new AccountInput();
        input.setFirstName("Jane");
        input.setLastName("Smith");

        Account account = RestAssured.given()
                .contentType("application/json")
                .body(input)
                .when()
                .post("/account")
                .then()
                .statusCode(200)
                .extract()
                .as(Account.class);

        double negativeAmount = 0;
        RestAssured.given()
                .contentType("application/json")
                .body(negativeAmount)
                .when()
                .post("/account/" + account.getAccountNumber() + "/deposit")
                .then()
                .statusCode(400)
                .body("error", containsString(("Amount must be greater than 0")));
    }

    @Test
    public void testDepositToNonExistingAccount() {
        String invalidAccountNumber = "non-existing-account";
        double depositAmount = 100.0;

        RestAssured.given()
                .contentType("application/json")
                .body(depositAmount)
                .when()
                .post("/account/" + invalidAccountNumber + "/deposit")
                .then()
                .statusCode(404);
    }
}
