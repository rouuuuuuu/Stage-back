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
    private final ClientRepository clientRepo;

    public ConsultationService(
        ConsultationClientRepository consultationRepo,
        ProduitRepository produitRepo,
        ClientRepository clientRepo
    ) {
        this.consultationRepo = consultationRepo;
        this.produitRepo = produitRepo;
        this.clientRepo = clientRepo;
    }

    // Récupérer toutes les consultations (Admin)
    public List<ConsultationClient> getAllConsultations() {
        return consultationRepo.findAll();
    }

    // Récupérer consultations par client
    public List<ConsultationClient> getConsultationsByClientId(Long clientId) {
        return consultationRepo.findByClientId(clientId);
    }

    // Création consultation avec association client + produits
    public ConsultationClient createConsultation(ConsultationClientDTO dto) {
        // Récupérer le client
        Client client = clientRepo.findById(dto.getClientId())
            .orElseThrow(() -> new IllegalArgumentException("Client introuvable avec id : " + dto.getClientId()));

        // Récupérer les produits demandés
        List<Produit> produits = dto.getProduitsIds().stream()
            .map(id -> produitRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable avec id : " + id)))
            .toList();

        // Création de la consultation
        ConsultationClient consultation = new ConsultationClient();
        consultation.setClient(client);
        consultation.setDescription(dto.getDescription());
        consultation.setProduitsDemandes(produits);
        consultation.setDateCreation(LocalDateTime.now());

        return consultationRepo.save(consultation);
    }
}
