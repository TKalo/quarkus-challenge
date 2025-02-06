package org.example.quarkuschallenge.currency;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Schema(description = "Supported currency codes for conversion.")
enum Currency {
    DKK,
    USD,
}

@Path("/currency")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CurrencyResource {

    @Inject
    CurrencyService currencyService;

    @GET
    @Path("/conversion/{fromCurrency}/{toCurrency}/{amount}")
    public double convertCurrency(
            @Parameter(description = "Currency to convert from", required = true) @PathParam("fromCurrency") Currency fromCurrency,
            @Parameter(description = "Currency to convert to", required = true) @PathParam("toCurrency") Currency toCurrency,
            @Parameter(description = "Amount to convert", required = true, example = "100.0") @PathParam("amount") double amount //
    ) {

        try {
            return currencyService.convertCurrency(amount, fromCurrency.name(), toCurrency.name());
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(e.getMessage())
                            .build());
        }
    }
}
