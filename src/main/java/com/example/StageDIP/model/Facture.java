package com.example.StageDIP.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private Double montantTotal;

    // Voilà ta devise, parce qu'on parle de monnaie ici, pas de rêves
    private String devise;

    // Et ton délai de livraison, parce que faut pas faire attendre la princesse
    private Integer delaiLivraison;

    // Many invoices belong to one supplier
    @ManyToOne
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;

    // One invoice can contain many products
    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL)
    private List<Produit> produits;

    public Facture() {}

    public Facture(LocalDate date, Double montantTotal, String devise, Integer delaiLivraison,
                   Fournisseur fournisseur, List<Produit> produits) {
        this.date = date;
        this.montantTotal = montantTotal;
        this.devise = devise;
        this.delaiLivraison = delaiLivraison;
        this.fournisseur = fournisseur;
        this.produits = produits;
    }

    // Getters & Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }

    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }

    public Integer getDelaiLivraison() { return delaiLivraison; }
    public void setDelaiLivraison(Integer delaiLivraison) { this.delaiLivraison = delaiLivraison; }

    public Fournisseur getFournisseur() { return fournisseur; }
    public void setFournisseur(Fournisseur fournisseur) { this.fournisseur = fournisseur; }

    public List<Produit> getProduits() { return produits; }
    public void setProduits(List<Produit> produits) { this.produits = produits; }
}
