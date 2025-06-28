package com.example.StageDIP.service;

import com.example.StageDIP.model.ConsultationClient;
import com.example.StageDIP.model.ConsultationClientDTO;
import com.example.StageDIP.model.Produit;
import com.example.StageDIP.repository.ConsultationClientRepository;
import com.example.StageDIP.repository.ProduitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultationService {

    private final ConsultationClientRepository consultationRepo;
    private final ProduitRepository produitRepo;

    public ConsultationService(ConsultationClientRepository consultationRepo, ProduitRepository produitRepo) {
        this.consultationRepo = consultationRepo;
        this.produitRepo = produitRepo;
    }

    public ConsultationClient createConsultation(ConsultationClientDTO dto) {
        List<Produit> produits = produitRepo.findAllById(dto.getProduitsIds());

        if (produits.size() != dto.getProduitsIds().size()) {
            throw new IllegalArgumentException("Un ou plusieurs produits sont introuvables.");
        }

        ConsultationClient consultation = new ConsultationClient();
        consultation.setClientId(dto.getClientId());
        consultation.setDescription(dto.getDescription());
        consultation.setProduitsDemandes(produits);

        return consultationRepo.save(consultation);
    }
}
