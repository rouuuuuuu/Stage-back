package com.example.StageDIP.dto;

import com.example.StageDIP.model.Fournisseur;
import com.example.StageDIP.model.Facture;

public class FournisseurTableDTO {

    private Long id;
    private String nom;
    private String adresse;
    private String email;
    private Double notation;

    private Double prix;
    private Integer delai;
    private String devise;

    public FournisseurTableDTO(Fournisseur fournisseur) {
        this.id = fournisseur.getId();
        this.nom = fournisseur.getNom();
        this.adresse = fournisseur.getAdresse();
        this.email = fournisseur.getEmail();
        this.notation = fournisseur.getNotation();

        // Use latest facture if it exists
        if (fournisseur.getFactures() != null && !fournisseur.getFactures().isEmpty()) {
            Facture latest = fournisseur.getFactures().stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .findFirst()
                .orElse(null);

            if (latest != null) {
                this.prix = latest.getPrixproduit();
                this.delai = latest.getDelaiLivraison();
                this.devise = "TND"; // 👈 update this if 'devise' is added in Facture class
            }
        }
    }

    // === Getters & Setters ===

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

    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }

    public Integer getDelai() { return delai; }
    public void setDelai(Integer delai) { this.delai = delai; }

    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
}
