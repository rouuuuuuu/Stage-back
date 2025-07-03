package com.example.StageDIP.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Table(name = "facture",
indexes = {
    @Index(name = "idx_devise", columnList = "devise"), //fix nahyyy el devise 
    @Index(name = "idx_delai_livraison", columnList = "delaiLivraison")
}
)


@Entity
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date date;

    private Double montantTotal;


    // Délai max de livraison en jours
    private Integer delaiLivraison;
    private Double  prixproduit;
    // Chaque facture appartient à un seul fournisseur
    @ManyToOne
    @JoinColumn(name = "fournisseur_id", nullable = false)
    private Fournisseur fournisseur;

    // Une facture peut contenir plusieurs produits
    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL)
    private Set<Produit> produits;

    // === Constructors ===
    public Facture() {}

    public Facture(Date date, Double montantTotal, Integer delaiLivraison,
                   Fournisseur fournisseur,Double prixproduit ) {
        this.date = date;
        this.montantTotal = montantTotal;
        this.delaiLivraison = delaiLivraison;
        this.fournisseur = fournisseur;
        this.prixproduit = prixproduit;
        
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }
    
    public Double getPrixproduit() { return prixproduit; }
    public void setPrixproduit(Double prixproduit) { this.prixproduit = prixproduit; }
    
    public Integer getDelaiLivraison() { return delaiLivraison; }
    public void setDelaiLivraison(Integer delaiLivraison) { this.delaiLivraison = delaiLivraison; }

    public Fournisseur getFournisseur() { return fournisseur; }
    public void setFournisseur(Fournisseur fournisseur) { this.fournisseur = fournisseur; }

    public Set<Produit> getProduits() { return produits; }
    public void setProduits(Set<Produit> produits) { this.produits = produits; }
}
