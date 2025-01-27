package org.example.bankdata;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.example.bankdata.account.Account;
import org.example.bankdata.account.AccountRepository;
import org.example.bankdata.account.input.AccountInput;
import org.example.bankdata.account.input.DepositeInput;
import org.example.bankdata.account.input.TransferInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
@QuarkusTestResource(PostgreSQLTestResource.class)
class AccountResourceTest {

        @Inject
        AccountRepository accountRepository;

        @BeforeEach
        @Transactional
        void purgeDatabase() {
                accountRepository.deleteAll();
        }

        @Test
        @Tag("create")
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
        @Tag("deposit")
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

                DepositeInput depositeInput = new DepositeInput();
                depositeInput.setAmount(100);
                RestAssured.given()
                                .contentType("application/json")
                                .body(depositeInput)
                                .when()
                                .post("/account/" + account.getAccountNumber() + "/deposit")
                                .then()
                                .statusCode(200)
                                .body("balance", equalTo((float) depositeInput.getAmount()));
        }

        @Test
        @Tag("deposit")
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

                DepositeInput depositeInput = new DepositeInput();
                depositeInput.setAmount(0);
                RestAssured.given()
                                .contentType("application/json")
                                .body(depositeInput)
                                .when()
                                .post("/account/" + account.getAccountNumber() + "/deposit")
                                .then()
                                .statusCode(400)
                                .body("error", containsString(("Amount must be greater than 0")));
        }

        @Test
        @Tag("deposit")
        public void testDepositToNonExistingAccount() {
                String invalidAccountNumber = "non-existing-account";
                DepositeInput depositeInput = new DepositeInput();
                depositeInput.setAmount(100);

                RestAssured.given()
                                .contentType("application/json")
                                .body(depositeInput)
                                .when()
                                .post("/account/" + invalidAccountNumber + "/deposit")
                                .then()
                                .statusCode(404);
        }

        @Test
        @Tag("transfer")
        public void testSuccessfulTransfer() {
                // Create source account
                AccountInput sourceInput = new AccountInput();
                sourceInput.setFirstName("John");
                sourceInput.setLastName("Doe");

                Account sourceAccount = RestAssured.given()
                                .contentType("application/json")
                                .body(sourceInput)
                                .when()
                                .post("/account")
                                .then()
                                .statusCode(200)
                                .extract()
                                .as(Account.class);

                // Deposit money into source account
                DepositeInput depositeInput = new DepositeInput();
                depositeInput.setAmount(100);
                RestAssured.given()
                                .contentType("application/json")
                                .body(depositeInput)
                                .when()
                                .post("/account/" + sourceAccount.getAccountNumber() + "/deposit")
                                .then()
                                .statusCode(200);

                // Create destination account
                AccountInput destinationInput = new AccountInput();
                destinationInput.setFirstName("Jane");
                destinationInput.setLastName("Smith");

                Account destinationAccount = RestAssured.given()
                                .contentType("application/json")
                                .body(destinationInput)
                                .when()
                                .post("/account")
                                .then()
                                .statusCode(200)
                                .extract()
                                .as(Account.class);

                // Transfer money
                TransferInput input = new TransferInput();
                input.setFromAccount(sourceAccount.getAccountNumber());
                input.setToAccount(destinationAccount.getAccountNumber());
                input.setAmount(50.0);

                RestAssured.given()
                                .contentType("application/json")
                                .body(input)
                                .when()
                                .post("/account/transfer")
                                .then()
                                .statusCode(200)
                                .body("message", equalTo("Transfer successful"));
        }

        @Test
        @Tag("transfer")
        public void testSourceAccountNotFound() {
                AccountInput sourceInput = new AccountInput();
                sourceInput.setFirstName("John");
                sourceInput.setLastName("Doe");

                Account validAccount = RestAssured.given()
                                .contentType("application/json")
                                .body(sourceInput)
                                .when()
                                .post("/account")
                                .then()
                                .statusCode(200)
                                .extract()
                                .as(Account.class);

                TransferInput input = new TransferInput();
                input.setFromAccount("non-existing-account");
                input.setToAccount(validAccount.getAccountNumber());
                input.setAmount(50.0);

                RestAssured.given()
                                .contentType("application/json")
                                .body(input)
                                .when()
                                .post("/account/transfer")
                                .then()
                                .statusCode(404)
                                .body("error", equalTo("Source account not found"));
        }

        @Test
        @Tag("transfer")
        public void testDestinationAccountNotFound() {
                AccountInput sourceInput = new AccountInput();
                sourceInput.setFirstName("John");
                sourceInput.setLastName("Doe");

                Account validAccount = RestAssured.given()
                                .contentType("application/json")
                                .body(sourceInput)
                                .when()
                                .post("/account")
                                .then()
                                .statusCode(200)
                                .extract()
                                .as(Account.class);

                TransferInput input = new TransferInput();
                input.setFromAccount(validAccount.getAccountNumber());
                input.setToAccount("non-existing-account");
                input.setAmount(50.0);

                RestAssured.given()
                                .contentType("application/json")
                                .body(input)
                                .when()
                                .post("/account/transfer")
                                .then()
                                .statusCode(404)
                                .body("error", equalTo("Destination account not found"));
        }

        @Test
        @Tag("transfer")
        public void testInsufficientFunds() {
                AccountInput sourceInput = new AccountInput();
                sourceInput.setFirstName("John");
                sourceInput.setLastName("Doe");

                Account sourceAccount = RestAssured.given()
                                .contentType("application/json")
                                .body(sourceInput)
                                .when()
                                .post("/account")
                                .then()
                                .statusCode(200)
                                .extract()
                                .as(Account.class);

                AccountInput destinationInput = new AccountInput();
                destinationInput.setFirstName("John");
                destinationInput.setLastName("Doe");

                Account destinationAccount = RestAssured.given()
                                .contentType("application/json")
                                .body(sourceInput)
                                .when()
                                .post("/account")
                                .then()
                                .statusCode(200)
                                .extract()
                                .as(Account.class);

                TransferInput input = new TransferInput();
                input.setFromAccount(sourceAccount.getAccountNumber());
                input.setToAccount(destinationAccount.getAccountNumber());
                input.setAmount(100.0);

                RestAssured.given()
                                .contentType("application/json")
                                .body(input)
                                .when()
                                .post("/account/transfer")
                                .then()
                                .statusCode(400)
                                .body("error", equalTo("Insufficient balance in source account"));
        }

        @Test
        @Tag("transfer")
        public void testZeroTransferAmount() {
                AccountInput sourceInput = new AccountInput();
                sourceInput.setFirstName("John");
                sourceInput.setLastName("Doe");

                Account sourceAccount = RestAssured.given()
                                .contentType("application/json")
                                .body(sourceInput)
                                .when()
                                .post("/account")
                                .then()
                                .statusCode(200)
                                .extract()
                                .as(Account.class);

                AccountInput destinationInput = new AccountInput();
                destinationInput.setFirstName("John");
                destinationInput.setLastName("Doe");

                Account destinationAccount = RestAssured.given()
                                .contentType("application/json")
                                .body(sourceInput)
                                .when()
                                .post("/account")
                                .then()
                                .statusCode(200)
                                .extract()
                                .as(Account.class);

                TransferInput input = new TransferInput();
                input.setFromAccount(sourceAccount.getAccountNumber());
                input.setToAccount(destinationAccount.getAccountNumber());
                input.setAmount(0);

                RestAssured.given()
                                .contentType("application/json")
                                .body(input)
                                .when()
                                .post("/account/transfer")
                                .then()
                                .statusCode(400)
                                .body("error", equalTo("Amount must be greater than 0"));
        }

        @Test
        @Tag("transfer")
        public void testSameSourceAndDestinationAccount() {
                AccountInput validInput = new AccountInput();
                validInput.setFirstName("John");
                validInput.setLastName("Doe");

                Account validAccount = RestAssured.given()
                                .contentType("application/json")
                                .body(validInput)
                                .when()
                                .post("/account")
                                .then()
                                .statusCode(200)
                                .extract()
                                .as(Account.class);

                TransferInput input = new TransferInput();
                input.setFromAccount(validAccount.getAccountNumber());
                input.setToAccount(validAccount.getAccountNumber());
                input.setAmount(50.0);

                RestAssured.given()
                                .contentType("application/json")
                                .body(input)
                                .when()
                                .post("/account/transfer")
                                .then()
                                .statusCode(400)
                                .body("error", equalTo("Source and destination accounts must be different"));
        }

        @Test
        @Tag("balance")
        public void testGetBalance() {
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

                RestAssured.given()
                                .when()
                                .get("/account/" + account.getAccountNumber() + "/balance")
                                .then()
                                .statusCode(200)
                                .body("balance", equalTo((float) 0.0));
        }

        @Test
        @Tag("balance")
        public void testGetUpdatedBalance() {
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

                DepositeInput depositeInput = new DepositeInput();
                depositeInput.setAmount(100);
                RestAssured.given()
                                .contentType("application/json")
                                .body(depositeInput)
                                .when()
                                .post("/account/" + account.getAccountNumber() + "/deposit")
                                .then()
                                .statusCode(200);

                RestAssured.given()
                                .when()
                                .get("/account/" + account.getAccountNumber() + "/balance")
                                .then()
                                .statusCode(200)
                                .body("balance", equalTo((float) depositeInput.getAmount()));
        }

        @Test
        @Tag("balance")
        public void testGetBalanceForNonExistingAccount() {
                String invalidAccountNumber = "non-existing-account";

                RestAssured.given()
                                .when()
                                .get("/account/" + invalidAccountNumber + "/balance")
                                .then()
                                .statusCode(404);
        }

        @Test
        @Tag("getAll")
        public void testGetAllAccounts() {
                AccountInput input1 = new AccountInput();
                input1.setFirstName("John");
                input1.setLastName("Doe");

                AccountInput input2 = new AccountInput();
                input2.setFirstName("Jane");
                input2.setLastName("Smith");

                RestAssured.given()
                                .contentType("application/json")
                                .body(input1)
                                .when()
                                .post("/account")
                                .then()
                                .statusCode(200);

                RestAssured.given()
                                .contentType("application/json")
                                .body(input2)
                                .when()
                                .post("/account")
                                .then()
                                .statusCode(200);

                RestAssured.given()
                                .when()
                                .get("/account")
                                .then()
                                .statusCode(200)
                                .body("size()", equalTo(2));
        }
}
