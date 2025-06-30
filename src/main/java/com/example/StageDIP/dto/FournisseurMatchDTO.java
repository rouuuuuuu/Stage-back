package com.example.StageDIP.dto;

import com.example.StageDIP.model.Fournisseur;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class FournisseurMatchDTO {

    @JsonIgnoreProperties({"produits", "factures", "hibernateLazyInitializer", "handler"})
    private Fournisseur fournisseur;

    private double prixTotal;
    private double couverture;
    private double score;

    // New additions:
    private int minDelai;
    private double notation;

    public FournisseurMatchDTO(Fournisseur fournisseur, double prixTotal, double couverture) {
        this.fournisseur = fournisseur;
        this.prixTotal = prixTotal;
        this.couverture = couverture;
    }

    // Getters and setters
    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
    }

    public double getCouverture() {
        return couverture;
    }

    public void setCouverture(double couverture) {
        this.couverture = couverture;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    // The new fields
    public int getMinDelai() {
        return minDelai;
    }

    public void setMinDelai(int minDelai) {
        this.minDelai = minDelai;
    }

    public double getNotation() {
        return notation;
    }

    public void setNotation(double notation) {
        this.notation = notation;
    }
}
