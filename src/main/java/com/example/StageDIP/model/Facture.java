package com.example.StageDIP.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Table(name = "facture",
indexes = {
    @Index(name = "idx_devise", columnList = "devise"),
    @Index(name = "idx_delai_livraison", columnList = "delaiLivraison")
}
)


@Entity
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private Double montantTotal;

    // Devise utilisée dans la facture (EUR, USD, etc.)
    private String devise;

    // Délai max de livraison en jours
    private Integer delaiLivraison;

    // Chaque facture appartient à un seul fournisseur
    @ManyToOne
    @JoinColumn(name = "fournisseur_id", nullable = false)
    private Fournisseur fournisseur;

    // Une facture peut contenir plusieurs produits
    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL)
    private Set<Produit> produits;

    // === Constructors ===
    public Facture() {}

    public Facture(LocalDate date, Double montantTotal, String devise, Integer delaiLivraison,
                   Fournisseur fournisseur, Set<Produit> produits) {
        this.date = date;
        this.montantTotal = montantTotal;
        this.devise = devise;
        this.delaiLivraison = delaiLivraison;
        this.fournisseur = fournisseur;
        this.produits = produits;
    }

    // === Getters & Setters ===
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

    public Set<Produit> getProduits() { return produits; }
    public void setProduits(Set<Produit> produits) { this.produits = produits; }
}
