package com.example.StageDIP.dto;

public class ProduitDTO {
    private Long id;
    private String nom;
    private String categorie;
    private Double prixUnitaire;
    private Long fournisseurId;
    private Long factureId;
    private String fournisseurNom;


    public ProduitDTO(Long id, String nom, String categorie, Double prixUnitaire, Long fournisseurId, Long factureId) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.prixUnitaire = prixUnitaire;
        this.fournisseurId = fournisseurId;
        this.factureId = factureId;
        
    }

    public ProduitDTO(com.example.StageDIP.model.Produit p) {
        this(p.getId(), p.getNom(), p.getCategorie(), p.getPrixUnitaire(),
             p.getFournisseur() != null ? p.getFournisseur().getId() : null,
             p.getFacture() != null ? p.getFacture().getId() : null);
        this.fournisseurNom = p.getFournisseur() != null ? p.getFournisseur().getNom() : null;  // <-- ADD THIS

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

    public Long getFournisseurId() { return fournisseurId; }
    public void setFournisseurId(Long fournisseurId) { this.fournisseurId = fournisseurId; }

    public Long getFactureId() { return factureId; }
    public void setFactureId(Long factureId) { this.factureId = factureId; }
    
    public String getFournisseurNom() { return fournisseurNom; }
    public void setFournisseurNom(String fournisseurNom) { this.fournisseurNom = fournisseurNom; }

}
