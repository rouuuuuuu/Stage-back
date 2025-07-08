package com.example.StageDIP.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

/**
 * Entity representing a Product (Produit)
 */
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Table(name = "produit",
    indexes = {
        @Index(name = "idx_prix_unitaire", columnList = "prixUnitaire"),
        @Index(name = "idx_categorie", columnList = "categorie")
    }

)
@Entity
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String categorie;
    private Double prixUnitaire;

    // Many products can belong to one supplier

    @ManyToOne
    @JoinColumn(name = "fournisseur_id")
    @JsonIgnoreProperties({"produits", "factures", "hibernateLazyInitializer", "handler"})
    private Fournisseur fournisseur;

    @ManyToOne
    @JoinColumn(name = "facture_id")
    @JsonIgnoreProperties({"produits", "factures", "hibernateLazyInitializer", "handler"})
    private Facture facture;

    public Produit() {}

    public Produit(String nom, String categorie, Double prixUnitaire, Fournisseur fournisseur,Facture facture) {
        this.nom = nom;
        this.categorie = categorie;
        this.prixUnitaire = prixUnitaire;
        this.fournisseur = fournisseur;
        this.facture = facture;
    }

    // Getters & Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public Double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public Fournisseur getFournisseur() { return fournisseur; }
    public void setFournisseur(Fournisseur fournisseur) { this.fournisseur = fournisseur; }
    
    public Facture getFacture() { return facture; }
    public void setFacture(Facture facture) {
        this.facture = facture;
        
    }
  
}
	
