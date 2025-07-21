package com.example.StageDIP.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ConsultationHistoryDTO {
    private Long consultationId;
    private String clientName;
    private String description;
    private List<String> produitsDemandes;
    private LocalDateTime dateCreation;
    
    // Facture details (optional, if present)
    private LocalDateTime factureDate;
    private Double factureMontantTotal;
    private String factureDevise;

    public ConsultationHistoryDTO(Long consultationId, String clientName, String description, List<String> produitsDemandes, 
                                 LocalDateTime dateCreation, LocalDateTime factureDate, Double factureMontantTotal, String factureDevise) {
        this.consultationId = consultationId;
        this.clientName = clientName;
        this.description = description;
        this.produitsDemandes = produitsDemandes;
        this.dateCreation = dateCreation;
        this.factureDate = factureDate;
        this.factureMontantTotal = factureMontantTotal;
        this.factureDevise = factureDevise;
    }

    // Getters and setters...
}
