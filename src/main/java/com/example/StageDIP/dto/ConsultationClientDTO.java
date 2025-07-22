package com.example.StageDIP.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class ConsultationClientDTO {

    @NotNull(message = "L'ID du client est obligatoire.")
    private Long clientId;

    @NotBlank(message = "La description est obligatoire.")
    @Size(min = 10, max = 1000, message = "La description doit contenir entre 10 et 1000 caractères.")
    private String description;

    @NotEmpty(message = "Au moins un produit doit être sélectionné.")
    private List<@NotNull(message = "L'ID du produit ne peut pas être nul.") Long> produitsIds;


    // Getters & Setters
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getProduitsIds() {
        return produitsIds;
    }

    public void setProduitsIds(List<Long> produitsIds) {
        this.produitsIds = produitsIds;
    }

  

   
}
