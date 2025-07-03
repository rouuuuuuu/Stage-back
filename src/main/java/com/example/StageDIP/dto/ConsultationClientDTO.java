package com.example.StageDIP.dto;

import java.util.List;

import com.example.StageDIP.model.Client;

public class ConsultationClientDTO {
	private Long clientId; 

	
    private String description;
    private List<Long> produitsIds;

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Long> getProduitsIds() { return produitsIds; }
    public void setProduitsIds(List<Long> produitsIds) { this.produitsIds = produitsIds; }

private Long fournisseurId;

public Long getFournisseurId() {
    return fournisseurId;
}

public void setFournisseurId(Long fournisseurId) {
    this.fournisseurId = fournisseurId;
}
}