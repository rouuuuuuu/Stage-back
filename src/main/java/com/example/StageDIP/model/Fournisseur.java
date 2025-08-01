package com.example.StageDIP.model;

import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Table(name = "fournisseur",
indexes = {
    @Index(name = "idx_notation", columnList = "notation")
}
)


@Entity
public class Fournisseur {

    // Primary key, auto-generated by the DB
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String adresse;
    private String email;
    private Double notation;
    private Double num;
    private Double fax;


    @OneToMany(mappedBy = "fournisseur", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"fournisseur", "facture", "hibernateLazyInitializer", "handler"})
    private Set<Produit> produits;

    @OneToMany(mappedBy = "fournisseur", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"fournisseur", "produits", "hibernateLazyInitializer", "handler"})
    private Set<Facture> factures;


    public Fournisseur() {}

    public Fournisseur(String nom, String adresse, String email, Double notation,Double num,Double fax) {
        this.nom = nom;
        this.adresse = adresse;
        this.email = email;
        this.num = num;
        this.fax = fax;
        this.notation = notation;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Double getNumero() { return num; }
    public void setNumero(Double num) { this.num = num; }
    
    public Double getFax() { return fax; }
    public void setFax(double fax) { this.fax = fax; }

    public Double getNotation() { return notation; }
    public void setNotation(Double notation) { this.notation = notation; }
    public Set<Facture> getFactures() {
        return factures;
    }

    public void setFactures(Set<Facture> factures) {
        this.factures = factures;
    }
   

    public Set<Produit> getProduits() {
        return produits;
    }

    public void setProduits(Set<Produit> produits) {
        this.produits = produits;
    }
    

}
