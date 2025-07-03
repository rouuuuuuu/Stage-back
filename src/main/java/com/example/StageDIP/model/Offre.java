package com.example.StageDIP.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long idfour;

    private Date dateoffre;
    private Date datevalidite;

    private String statut;


    
    @ManyToOne
    @JoinColumn(name = "fournisseur_id", nullable = false)
    private Fournisseur fournisseur;

  

    // === Constructors ===
    public Offre() {}

    public Offre(Date dateoffre, Date datevalidite,Long idfour, String statut,
                   Fournisseur fournisseur ) {
        this.dateoffre = dateoffre;
        this.datevalidite = datevalidite;

        this.idfour = idfour;
        this.statut = statut;
        this.fournisseur = fournisseur;
        
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getDateoffre() { return dateoffre; }
    public void setDateoffre(Date dateoffre) { this.dateoffre = dateoffre; }

    public Date getDatevalidite() { return datevalidite; }
    public void setDatevalidite(Date datevalidite) { this.datevalidite = datevalidite; }

    public Long getIdfour() { return idfour; }
    public void setIdfour(Long idfour) { this.idfour = idfour; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
   
    public Fournisseur getFournisseur() { return fournisseur; }
    public void setFournisseur(Fournisseur fournisseur) { this.fournisseur = fournisseur; }

	
	}

    
