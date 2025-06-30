package com.example.StageDIP.service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.StageDIP.model.Fournisseur;

import com.example.StageDIP.repository.FournisseurRepository;
import com.example.StageDIP.repository.FournisseurSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
public class FournisseurService {

    private final FournisseurRepository repo;

    public FournisseurService(FournisseurRepository repo) {
        this.repo = repo;
    }

    public List<Fournisseur> getAll() {
        return repo.findAll();
    }

    public Fournisseur save(Fournisseur f) {
        return repo.save(f);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
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


    public Page<Fournisseur> filterFournisseurs(
    	    Double minPrix, Double maxPrix, Double minNotation,
    	    String categorie, String nomProduit, String devise,
    	    Integer maxDelai, Pageable pageable) {

    	    Specification<Fournisseur> spec = Specification.where(null);

    	    spec = spec.and(FournisseurSpecifications.prixProduitBetween(minPrix, maxPrix));
    	    spec = spec.and(FournisseurSpecifications.notationMin(minNotation));
    	    spec = spec.and(FournisseurSpecifications.categorieProduitEgale(categorie));
    	    spec = spec.and(FournisseurSpecifications.nomProduitContient(nomProduit));
    	    spec = spec.and(FournisseurSpecifications.deviseFactureEgale(devise));
    	    spec = spec.and(FournisseurSpecifications.delaiLivraisonMax(maxDelai));

    	    return repo.findAll(spec, pageable);
    	}

}
