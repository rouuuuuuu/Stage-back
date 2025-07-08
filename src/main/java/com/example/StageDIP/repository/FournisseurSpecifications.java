package com.example.StageDIP.repository;

import com.example.StageDIP.model.Fournisseur;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

public class FournisseurSpecifications {

	public Specification<Fournisseur> buildFilterSpec(
		    Double minPrix, Double maxPrix,
		    Double minNotation,
		    String categorie, String nomProduit,
		    String devise,
		    Integer maxDelai) {
		    
		    return (root, query, builder) -> {
		        // Make query distinct because of joins
		        query.distinct(true);

		        // Create joins once, reuse
		        Join<?, ?> produits = root.join("produits", JoinType.LEFT);
		        Join<?, ?> factures = root.join("factures", JoinType.LEFT);

		        List<Predicate> predicates = new ArrayList<>();

		        // Prix filter on produits.prixUnitaire
		        if (minPrix != null && maxPrix != null) {
		            predicates.add(builder.between(produits.get("prixUnitaire"), minPrix, maxPrix));
		        } else if (minPrix != null) {
		            predicates.add(builder.greaterThanOrEqualTo(produits.get("prixUnitaire"), minPrix));
		        } else if (maxPrix != null) {
		            predicates.add(builder.lessThanOrEqualTo(produits.get("prixUnitaire"), maxPrix));
		        }

		        // Notation on root
		        if (minNotation != null) {
		            predicates.add(builder.greaterThanOrEqualTo(root.get("notation"), minNotation));
		        }

		        // Categorie filter on produits.categorie
		        if (categorie != null && !categorie.isEmpty()) {
		            predicates.add(builder.equal(produits.get("categorie"), categorie));
		        }

		        // Nom produit contains
		        if (nomProduit != null && !nomProduit.isEmpty()) {
		            predicates.add(builder.like(builder.lower(produits.get("nom")), "%" + nomProduit.toLowerCase() + "%"));
		        }

		        // Delai livraison max on factures.delaiLivraison
		        if (maxDelai != null) {
		            predicates.add(builder.lessThanOrEqualTo(factures.get("delaiLivraison"), maxDelai));
		        }

		        // Add more predicates if devise filtering needed

		        return builder.and(predicates.toArray(new Predicate[0]));
		    };
		}
}