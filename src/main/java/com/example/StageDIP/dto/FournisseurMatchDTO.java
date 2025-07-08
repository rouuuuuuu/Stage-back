package com.example.StageDIP.dto;

import com.example.StageDIP.model.Fournisseur;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class FournisseurMatchDTO {

    @JsonIgnoreProperties({"produits", "factures", "hibernateLazyInitializer", "handler"})
    private Fournisseur fournisseur;
    
    private Long id;
    private String nom;
    private String adresse;
    private String email;

    private double prixTotal;
    private double couverture;
    private double score;
    private int minDelai;
    private double notation;

    // Constructor - fill everything explicitly
    public FournisseurMatchDTO(Long id, String nom, String adresse, String email,
                              double prixTotal, double couverture, double score,
                              int minDelai, double notation) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.email = email;
        this.prixTotal = prixTotal;
        this.couverture = couverture;
        this.score = score;
        this.minDelai = minDelai;
        this.notation = notation;
    }


    

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
