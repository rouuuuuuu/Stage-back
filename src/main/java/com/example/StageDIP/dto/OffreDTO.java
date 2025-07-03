package com.example.StageDIP.dto;

import java.util.Date;

public class OffreDTO {

    private Long id;
    private Date dateoffre;
    private Date datevalidite;
    private String statut;
    private Long fournisseurId;

    // Constructors
    public OffreDTO() {}

    public OffreDTO(Long id, Date dateoffre, Date datevalidite, String statut, Long fournisseurId) {
        this.id = id;
        this.dateoffre = dateoffre;
        this.datevalidite = datevalidite;
        this.statut = statut;
        this.fournisseurId = fournisseurId;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateoffre() {
        return dateoffre;
    }

    public void setDateoffre(Date dateoffre) {
        this.dateoffre = dateoffre;
    }

    public Date getDatevalidite() {
        return datevalidite;
    }

    public void setDatevalidite(Date datevalidite) {
        this.datevalidite = datevalidite;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Long getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(Long fournisseurId) {
        this.fournisseurId = fournisseurId;
    }
}
