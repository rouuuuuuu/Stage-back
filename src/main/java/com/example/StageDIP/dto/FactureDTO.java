package com.example.StageDIP.dto;

import java.util.Date;
import java.util.List;

public class FactureDTO {
    private Date date;
    private Double montantTotal;
    private Integer delaiLivraison;
    private Double prixproduit;
    private Long fournisseurId;
    private List<Long> produitIds;

    // Getters and setters
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }

    public Integer getDelaiLivraison() { return delaiLivraison; }
    public void setDelaiLivraison(Integer delaiLivraison) { this.delaiLivraison = delaiLivraison; }

    public Double getPrixproduit() { return prixproduit; }
    public void setPrixproduit(Double prixproduit) { this.prixproduit = prixproduit; }

    public Long getFournisseurId() { return fournisseurId; }
    public void setFournisseurId(Long fournisseurId) { this.fournisseurId = fournisseurId; }

    public List<Long> getProduitIds() { return produitIds; }
    public void setProduitIds(List<Long> produitIds) { this.produitIds = produitIds; }
}
