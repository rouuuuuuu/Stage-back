// src/main/java/com/example/gestionfournisseurs/dto/ConsultationClientDTO.java
package com.example.StageDIP.model;

import java.util.List;

public class ConsultationClientDTO {
    private Long clientId;
    private String description;
    private List<Long> produitsIds;

    // Getters et Setters
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Long> getProduitsIds() { return produitsIds; }
    public void setProduitsIds(List<Long> produitsIds) { this.produitsIds = produitsIds; }
}