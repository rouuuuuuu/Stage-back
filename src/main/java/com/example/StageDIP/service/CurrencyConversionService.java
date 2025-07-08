package com.example.StageDIP.service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyConversionService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiKey = "d8bb6cc33d-50ac6db32e-syn6m0";
    private final String baseUrl = "https://openexchangerates.org/api/latest.json?app_id=";

    private Map<String, Double> ratesCache = new HashMap<>();
    private Instant lastFetch = Instant.MIN;

    public double convert(double amount, String fromCurrency, String toCurrency) {
        if (ratesCache.isEmpty() || Duration.between(lastFetch, Instant.now()).toHours() > 1) {
            fetchRates();
        }
        double fromRate = ratesCache.getOrDefault(fromCurrency.toUpperCase(), 1.0);
        double toRate = ratesCache.getOrDefault(toCurrency.toUpperCase(), 1.0);
        return amount / fromRate * toRate;
    }

    private void fetchRates() {
        String url = baseUrl + apiKey;
        Map response = restTemplate.getForObject(url, Map.class);
        ratesCache = (Map<String, Double>) response.get("rates");
        lastFetch = Instant.now();
    }
}
