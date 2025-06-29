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

            Join<Fournisseur, Produit> produits = root.join("produits", JoinType.INNER);

            if (minPrix == null) return builder.le(produits.get("prixUnitaire"), maxPrix);
            if (maxPrix == null) return builder.ge(produits.get("prixUnitaire"), minPrix);
            return builder.between(produits.get("prixUnitaire"), minPrix, maxPrix);
        };
    }

    public static Specification<Fournisseur> notationMin(Double minNotation) {
        return (root, query, builder) -> {
            if (minNotation == null) return null;
            return builder.ge(root.get("notation"), minNotation);
        };
    }

    public static Specification<Fournisseur> categorieProduitEgale(String categorie) {
        return (root, query, builder) -> {
            if (categorie == null || categorie.isEmpty()) return null;
            Join<Fournisseur, Produit> produits = root.join("produits", JoinType.INNER);
            return builder.equal(produits.get("categorie"), categorie);
        };
    }

    public static Specification<Fournisseur> nomProduitContient(String motCle) {
        return (root, query, builder) -> {
            if (motCle == null || motCle.isEmpty()) return null;
            Join<Fournisseur, Produit> produits = root.join("produits", JoinType.INNER);
            return builder.like(builder.lower(produits.get("nom")), "%" + motCle.toLowerCase() + "%");
        };
    }

    public static Specification<Fournisseur> deviseFactureEgale(String devise) {
        return (root, query, builder) -> {
            if (devise == null || devise.isEmpty()) return null;

            Join<Fournisseur, Facture> factures = root.join("factures", JoinType.INNER);
            return builder.equal(factures.get("devise"), devise);
        };
    }
}
