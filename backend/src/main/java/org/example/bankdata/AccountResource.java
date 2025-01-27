package org.example.bankdata;

import java.util.Map;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/account")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    @Inject
    AccountService accountService;

    @POST
    public Account createAccount(AccountInput input) {
        try {
            return accountService.createAccount(input);
        } catch (Exception e) {
            e.printStackTrace();
            throw new jakarta.ws.rs.WebApplicationException(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
        }
    }

    @POST
    @Path("/{accountNumber}/deposit")
    public Account depositMoney(@PathParam("accountNumber") String accountNumber, double amount) {
        try {
            return accountService.depositMoney(accountNumber, amount);
        } catch (NotFoundException e) {
            throw new jakarta.ws.rs.WebApplicationException(
                    Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            throw new jakarta.ws.rs.WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error",
                            "Amount must be greater than 0")).build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new jakarta.ws.rs.WebApplicationException(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
        }
    }

    @POST
    @Path("/transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response transferMoney(TransferInput input) {
        try {
            accountService.transferMoney(input.getFromAccount(), input.getToAccount(), input.getAmount());
            return Response.ok(Map.of("message", "Transfer successful")).build();
        } catch (NotFoundException e) {
            if (e.getMessage() == "Source account not found") {
                return Response.status(Response.Status.NOT_FOUND).entity(Map.of("error", "Source account not found"))
                        .build();
            } else if (e.getMessage() == "Destination account not found") {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Destination account not found")).build();
            } else {
                throw e;
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage() == "Amount must be greater than 0") {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Amount must be greater than 0"))
                        .build();
            } else if (e.getMessage() == "Insufficient balance in source account") {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Insufficient balance in source account")).build();
            } else if (e.getMessage() == "Source and destination accounts must be different") {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Source and destination accounts must be different")).build();
            } else {
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{accountNumber}/balance")
    public Response getAccountBalance(@PathParam("accountNumber") String accountNumber) {
        try {
            Account account = accountService.getAccount(accountNumber);
            return Response.ok(Map.of("balance", account.getBalance())).build();
        } catch (NotFoundException e) {
            throw new jakarta.ws.rs.WebApplicationException(
                    Response.status(Response.Status.NOT_FOUND).build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new jakarta.ws.rs.WebApplicationException(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
        }
    }

    @GET
    public Response getAllAccounts() {
        try {
            return Response.ok(accountService.getAllAccounts()).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new jakarta.ws.rs.WebApplicationException(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
        }
    }
}
