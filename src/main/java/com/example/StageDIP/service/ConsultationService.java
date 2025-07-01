package com.example.StageDIP.service;

import com.example.StageDIP.dto.ConsultationClientDTO;
import com.example.StageDIP.model.Client;
import com.example.StageDIP.model.ConsultationClient;
import com.example.StageDIP.model.Produit;
import com.example.StageDIP.repository.ClientRepository;
import com.example.StageDIP.repository.ConsultationClientRepository;
import com.example.StageDIP.repository.ProduitRepository;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import java.util.List;

@Service
public class ConsultationService {

    private final ConsultationClientRepository consultationRepo;
    private final ProduitRepository produitRepo;
	private ClientRepository clientRepo;

    public ConsultationService(
    	    ConsultationClientRepository consultationRepo, 
    	    ProduitRepository produitRepo,
    	    ClientRepository clientRepo
    	) {
    	    this.consultationRepo = consultationRepo;
    	    this.produitRepo = produitRepo;
    	    this.clientRepo = clientRepo;
    	}


    public List<ConsultationClient> getAllConsultations() {
        return consultationRepo.findAll();
    }


    public ConsultationClient createConsultation(ConsultationClientDTO dto) {
        // Récupérer les produits
        List<Produit> produits = dto.getProduitsIds().stream()
            .map(id -> produitRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable avec id : " + id)))
            .toList();

        // Récupérer le client
        Client client = clientRepo.findById(dto.getClientId())
            .orElseThrow(() -> new IllegalArgumentException("Client introuvable avec id : " + dto.getClientId()));

        // Créer la consultation ET set client dedans
        ConsultationClient consultation = new ConsultationClient();
        consultation.setClient(client);
        consultation.setDescription(dto.getDescription());
        consultation.setProduitsDemandes(produits);
        consultation.setDateCreation(LocalDateTime.now());

        return consultationRepo.save(consultation);
    }
}