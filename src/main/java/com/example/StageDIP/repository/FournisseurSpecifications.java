package com.example.StageDIP.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.StageDIP.model.Facture;
import com.example.StageDIP.model.Fournisseur;
import com.example.StageDIP.model.Produit;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class FournisseurSpecifications {

    public static Specification<Fournisseur> filter(
            Double minMontantTotalDernier,
            Double maxMontantTotalDernier,
            Double minNotation,
            String categorie,
            String nomProduit,
            String devise,
            Integer maxDelai
    ) {
        return (root, query, cb) -> {
            // To avoid duplicates because of joins
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            if (minNotation != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("notation"), minNotation));
            }

            if (categorie != null) {
                Join<Fournisseur, Produit> produitJoin = root.join("produits", JoinType.LEFT);
                predicates.add(cb.equal(produitJoin.get("categorie"), categorie));
            }

            if (nomProduit != null) {
                Join<Fournisseur, Produit> produitJoin = root.join("produits", JoinType.LEFT);
                predicates.add(cb.like(cb.lower(produitJoin.get("nom")), "%" + nomProduit.toLowerCase() + "%"));
            }

            if (devise != null) {
                Join<Fournisseur, Facture> factureJoin = root.join("factures", JoinType.LEFT);
                predicates.add(cb.equal(factureJoin.get("devise"), devise));
            }

            if (maxDelai != null) {
                Join<Fournisseur, Facture> factureJoin = root.join("factures", JoinType.LEFT);
                predicates.add(cb.lessThanOrEqualTo(factureJoin.get("delaiLivraison"), maxDelai));
            }

            if (minMontantTotalDernier != null) {
                Join<Fournisseur, Facture> factureJoin = root.join("factures", JoinType.LEFT);
                predicates.add(cb.greaterThanOrEqualTo(factureJoin.get("montantTotal"), minMontantTotalDernier));
            }

            if (maxMontantTotalDernier != null) {
                Join<Fournisseur, Facture> factureJoin = root.join("factures", JoinType.LEFT);
                predicates.add(cb.lessThanOrEqualTo(factureJoin.get("montantTotal"), maxMontantTotalDernier));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
