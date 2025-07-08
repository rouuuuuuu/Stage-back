package com.example.StageDIP.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.StageDIP.model.Fournisseur;
import com.example.StageDIP.repository.FournisseurRepository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

@Service
public class FournisseurService {

    private final FournisseurRepository repo;
    private final CurrencyConversionService currencyConversionService;

    public FournisseurService(FournisseurRepository repo, CurrencyConversionService currencyConversionService) {
        this.repo = repo;
        this.currencyConversionService = currencyConversionService;
    }

    public Page<Fournisseur> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Fournisseur save(Fournisseur f) {
        return repo.save(f);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    // The all-in-one specification builder for your filters
    private Specification<Fournisseur> buildFilterSpec(
    	    Double minPrix, Double maxPrix,
    	    Double minNotation,
    	    String categorie, String nomProduit,
    	    String devise,
    	    Integer maxDelai) {

    	    return (root, query, builder) -> {
    	        query.distinct(true);

    	        List<Predicate> predicates = new ArrayList<>();

    	        // Notation filter on fournisseur (root)
    	        if (minNotation != null) {
    	            predicates.add(builder.greaterThanOrEqualTo(root.get("notation"), minNotation));
    	        }

    	        // Filter on produits (price, category, name)
    	        if (minPrix != null || maxPrix != null || (categorie != null && !categorie.isEmpty()) || (nomProduit != null && !nomProduit.isEmpty())) {
    	            Join<?, ?> produits = root.join("produits", JoinType.LEFT);

    	            if (minPrix != null && maxPrix != null) {
    	                predicates.add(builder.between(produits.get("prixUnitaire"), minPrix, maxPrix));
    	            } else if (minPrix != null) {
    	                predicates.add(builder.greaterThanOrEqualTo(produits.get("prixUnitaire"), minPrix));
    	            } else if (maxPrix != null) {
    	                predicates.add(builder.lessThanOrEqualTo(produits.get("prixUnitaire"), maxPrix));
    	            }

    	            if (categorie != null && !categorie.isEmpty()) {
    	                predicates.add(builder.equal(produits.get("categorie"), categorie));
    	            }

    	            if (nomProduit != null && !nomProduit.isEmpty()) {
    	                predicates.add(builder.like(builder.lower(produits.get("nom")), "%" + nomProduit.toLowerCase() + "%"));
    	            }
    	        }

    	        // Filter on factures (delivery delay, devise)
    	        if (maxDelai != null || (devise != null && !devise.isEmpty())) {
    	            Join<?, ?> factures = root.join("factures", JoinType.LEFT);

    	            if (maxDelai != null) {
    	                predicates.add(builder.lessThanOrEqualTo(factures.get("delaiLivraison"), maxDelai));
    	            }

    	            if (devise != null && !devise.isEmpty()) {
    	                predicates.add(builder.equal(factures.get("devise"), devise));
    	            }
    	        }

    	        return builder.and(predicates.toArray(new Predicate[0]));
    	    };
    	}

    // Your public filter method to call from controller/service layer
    public Page<Fournisseur> filterFournisseurs(
        Double minPrix, Double maxPrix,
        Double minNotation,
        String categorie, String nomProduit,
        String devise,
        Integer maxDelai,
        Pageable pageable) {

        Specification<Fournisseur> spec = buildFilterSpec(minPrix, maxPrix, minNotation, categorie, nomProduit, devise, maxDelai);
        return repo.findAll(spec, pageable);
    }

    public Fournisseur update(Long id, Fournisseur fournisseurData) {
        Optional<Fournisseur> optional = repo.findById(id);
        if (optional.isEmpty()) return null;

        Fournisseur existing = optional.get();
        existing.setNom(fournisseurData.getNom());
        existing.setAdresse(fournisseurData.getAdresse());
        existing.setEmail(fournisseurData.getEmail());
        existing.setNotation(fournisseurData.getNotation());

        return repo.save(existing);
    }

    public List<Fournisseur> getAllFlat() {
        return repo.findAll(); // assuming eager fetch or @EntityGraph on repo
    }
}
