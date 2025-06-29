package com.example.StageDIP.dto;

import com.example.StageDIP.model.Fournisseur;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class FournisseurMatchDTO {

    // Prevent infinite loop if Fournisseur has lazy-loaded fields
    @JsonIgnoreProperties({"produits", "factures", "hibernateLazyInitializer", "handler"})
    private Fournisseur fournisseur;

    private double prixTotal;
    private double couverture;

    public FournisseurMatchDTO(Fournisseur fournisseur, double prixTotal, double couverture) {
        this.fournisseur = fournisseur;
        this.prixTotal = prixTotal;
        this.couverture = couverture;
    }

    // Getters
    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public double getPrixTotal() {
        return prixTotal;
    }

    public double getCouverture() {
        return couverture;
    }

    // Optional setters (just in case)
    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
    }

    public void setCouverture(double couverture) {
        this.couverture = couverture;
    }
}
