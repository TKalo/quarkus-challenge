package org.example.bankdata.currency;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONException;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CurrencyService {

    @ConfigProperty(name = "exchange-rate.api-key")
    String apiKey;

    @ConfigProperty(name = "exchange-rate.api-url")
    String apiUrl;

    public double convertCurrency(double amount, String baseCurrency, String targetCurrency) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("API key is not set in the environment variables");
        }

        try {
            String requestUrl = apiUrl + apiKey + "/pair/" + baseCurrency + "/" + targetCurrency + "/" + amount;
            URL url = new URL(requestUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Failed to fetch exchange rate: HTTP code " + responseCode);
            }

            StringBuilder response = new StringBuilder();
            try (Scanner scanner = new Scanner(url.openStream())) {
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
            }

            String jsonResponse = response.toString();
            int amountIndex = jsonResponse.indexOf("\"conversion_result\":");
            if (amountIndex == -1) {
                throw new RuntimeException("Invalid response from the API: " + jsonResponse);
            }
            JSONObject jsonObject = new JSONObject(jsonResponse);
            double conversionResult = jsonObject.getDouble("conversion_result");
            return conversionResult;

        } catch (IOException e) {
            System.out.println("Error while calling the exchange rate API: " + e.getMessage());
            throw new RuntimeException("Error while calling the exchange rate API: " + e.getMessage(), e);
        }
    }
}
