package com.example.StageDIP.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "consultations_client")
public class ConsultationClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER) // EAGER to auto-fetch client details
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;  
   
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "facture_id")
    private Facture facture;


    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    private Date delai ;
    @ManyToMany
    @JoinTable(
        name = "consultation_produits",
        joinColumns = @JoinColumn(name = "consultation_id"),
        inverseJoinColumns = @JoinColumn(name = "produit_id")
    )
    
    private List<Produit> produitsDemandes;

    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    public ConsultationClient() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Date getDelai() { return delai; }
    public void setDelai(Date delai) { this.delai = delai; }
    
    public List<Produit> getProduitsDemandes() { return produitsDemandes; }
    public void setProduitsDemandes(List<Produit> produitsDemandes) { this.produitsDemandes = produitsDemandes; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public Facture getFacture() { return facture; }

    public void setFacture(Facture facture) {
        this.facture = facture;
    }

}