package com.example.StageDIP.dto;

import com.example.StageDIP.model.Facture;
import com.example.StageDIP.model.Fournisseur;

public class FournisseurTableDTO {

    private Long id;
    private String nom;
    private String adresse;
    private String email;
    private Double notation;

    private Double montantTotalDernier;
    private Integer delaiLivraisonDernier;
    private String devise;

    public FournisseurTableDTO(Fournisseur fournisseur) {
        this.id = fournisseur.getId();
        this.nom = fournisseur.getNom();
        this.adresse = fournisseur.getAdresse();
        this.email = fournisseur.getEmail();
        this.notation = fournisseur.getNotation();

        if (fournisseur.getFactures() != null && !fournisseur.getFactures().isEmpty()) {
            Facture latest = fournisseur.getFactures().stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .findFirst()
                .orElse(null);

            if (latest != null) {
                this.montantTotalDernier = latest.getMontantTotal();
                this.delaiLivraisonDernier = latest.getDelaiLivraison();
                this.devise = "TND"; // or latest.getDevise() if available
            }
        }
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Double getNotation() { return notation; }
    public void setNotation(Double notation) { this.notation = notation; }

    public Double getMontantTotalDernier() { return montantTotalDernier; }
    public void setMontantTotalDernier(Double montantTotalDernier) { this.montantTotalDernier = montantTotalDernier; }

    public Integer getDelaiLivraisonDernier() { return delaiLivraisonDernier; }
    public void setDelaiLivraisonDernier(Integer delaiLivraisonDernier) { this.delaiLivraisonDernier = delaiLivraisonDernier; }

    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
}
