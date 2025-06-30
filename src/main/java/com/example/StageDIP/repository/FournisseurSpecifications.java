package com.example.StageDIP.repository;

import com.example.StageDIP.model.Fournisseur;
import com.example.StageDIP.model.Produit;
import com.example.StageDIP.model.Facture;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class FournisseurSpecifications {

    public static Specification<Fournisseur> prixProduitBetween(Double minPrix, Double maxPrix) {
        return (root, query, builder) -> {
            if (minPrix == null && maxPrix == null) return null;

            query.distinct(true);
            Join<?, ?> produits = root.join("produits", JoinType.LEFT);

            if (minPrix == null) return builder.lessThanOrEqualTo(produits.get("prixUnitaire"), maxPrix);
            if (maxPrix == null) return builder.greaterThanOrEqualTo(produits.get("prixUnitaire"), minPrix);
            return builder.between(produits.get("prixUnitaire"), minPrix, maxPrix);
        };
    }

    public static Specification<Fournisseur> notationMin(Double minNotation) {
        return (root, query, builder) -> {
            if (minNotation == null) return null;
            return builder.greaterThanOrEqualTo(root.get("notation"), minNotation);
        };
    }

    public static Specification<Fournisseur> categorieProduitEgale(String categorie) {
        return (root, query, builder) -> {
            if (categorie == null || categorie.isEmpty()) return null;

            query.distinct(true);
            Join<?, ?> produits = root.join("produits", JoinType.LEFT);
            return builder.equal(produits.get("categorie"), categorie);
        };
    }

    public static Specification<Fournisseur> nomProduitContient(String motCle) {
        return (root, query, builder) -> {
            if (motCle == null || motCle.isEmpty()) return null;

            query.distinct(true);
            Join<?, ?> produits = root.join("produits", JoinType.LEFT);
            return builder.like(builder.lower(produits.get("nom")), "%" + motCle.toLowerCase() + "%");
        };
    }

    public static Specification<Fournisseur> deviseFactureEgale(String devise) {
        return (root, query, builder) -> {
            if (devise == null || devise.isEmpty()) return null;

            query.distinct(true);
            Join<?, ?> factures = root.join("factures", JoinType.LEFT);
            return builder.equal(factures.get("devise"), devise);
        };
    }

    public static Specification<Fournisseur> delaiLivraisonMax(Integer maxDelai) {
        return (root, query, builder) -> {
            if (maxDelai == null) return null;

            query.distinct(true);
            Join<?, ?> factures = root.join("factures", JoinType.LEFT);
            return builder.lessThanOrEqualTo(factures.get("delaiLivraison"), maxDelai);
        };
    }
}
