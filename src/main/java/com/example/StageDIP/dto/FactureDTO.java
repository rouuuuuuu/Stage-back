package com.example.StageDIP.dto;

import jakarta.validation.constraints.*;
import java.util.Date;
import java.util.List;

public class FactureDTO {

    @NotNull(message = "La date de la facture est obligatoire.")
    private Date date;

    @NotNull(message = "Le montant total est obligatoire.")
    @PositiveOrZero(message = "Le montant total ne peut pas être négatif.")
    private Double montantTotal;

    @NotNull(message = "Le délai de livraison est obligatoire.")
    @Min(value = 0, message = "Le délai de livraison doit être supérieur ou égal à 0.")
    private Integer delaiLivraison;

    @NotNull(message = "Le prix du produit est obligatoire.")
    @Positive(message = "Le prix du produit doit être positif.")
    private Double prixproduit;

    @NotNull(message = "L'ID du fournisseur est obligatoire.")
    private Long fournisseurId;

    @NotEmpty(message = "La facture doit contenir au moins un produit.")
    private List<@NotNull(message = "Chaque ID de produit est requis.") Long> produitIds;

    @NotBlank(message = "La devise est obligatoire.")
    private String devise;

    // Getters & Setters
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

    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
}
