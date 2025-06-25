package com.example.StageDIP.service;

import com.example.StageDIP.model.ConsultationClientDTO;
import com.example.StageDIP.model.ConsultationClient;
import com.example.StageDIP.model.Produit;
import com.example.StageDIP.repository.ConsultationRepository;
import com.example.StageDIP.repository.ProduitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final ProduitRepository produitRepository;

    public ConsultationService(ConsultationRepository consultationRepository, 
                             ProduitRepository produitRepository) {
        this.consultationRepository = consultationRepository;
        this.produitRepository = produitRepository;
    }

    @Transactional
    public ConsultationClient createConsultation(ConsultationClientDTO dto) {

    	List<Produit> produits = produitRepository.findAllById(dto.getProduitsIds());
        if (produits.size() != dto.getProduitsIds().size()) {
            throw new IllegalArgumentException("Un ou plusieurs produits n'existent pas");
        }

        ConsultationClient consultation = new ConsultationClient();
        consultation.setClientId(dto.getClientId());
        consultation.setDescription(dto.getDescription());
        consultation.setProduitsDemandes(produits);

        return consultationRepository.save(consultation);
    }
}