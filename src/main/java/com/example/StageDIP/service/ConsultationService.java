package com.example.StageDIP.service;

import com.example.StageDIP.dto.ConsultationClientDTO;
import com.example.StageDIP.dto.ConsultationHistoryDTO;
import com.example.StageDIP.model.Client;
import com.example.StageDIP.model.ConsultationClient;
import com.example.StageDIP.model.Facture;
import com.example.StageDIP.model.Produit;
import com.example.StageDIP.repository.ClientRepository;
import com.example.StageDIP.repository.ConsultationClientRepository;
import com.example.StageDIP.repository.FactureRepository;
import com.example.StageDIP.repository.FournisseurRepository;
import com.example.StageDIP.repository.ProduitRepository;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConsultationService {

    private final ConsultationClientRepository consultationRepo;
    private final ProduitRepository produitRepo;
    private final ClientRepository clientRepo;
    private final FactureRepository factureRepo;
    private final FournisseurRepository fournisseurRepo;

    public ConsultationService(
            ConsultationClientRepository consultationRepo,
            ProduitRepository produitRepo,
            ClientRepository clientRepo,
            FactureRepository factureRepo,
            FournisseurRepository fournisseurRepo
    ) {
        this.consultationRepo = consultationRepo;
        this.produitRepo = produitRepo;
        this.clientRepo = clientRepo;
        this.factureRepo = factureRepo;
        this.fournisseurRepo = fournisseurRepo;
    }

    @Transactional
    public ConsultationClient createConsultation(ConsultationClientDTO dto) {
        if (dto.getClientId() == null) {
            throw new IllegalArgumentException("Client ID cannot be null!");
        }
        if (dto.getProduitsIds() == null || dto.getProduitsIds().isEmpty()) {
            throw new IllegalArgumentException("Produits list cannot be null or empty!");
        }

        Client client = clientRepo.findById(dto.getClientId())
                .orElseThrow(() -> new IllegalArgumentException("Client introuvable avec id : " + dto.getClientId()));

        List<Produit> produits = dto.getProduitsIds().stream()
                .map(id -> produitRepo.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Produit introuvable avec id : " + id)))
                .collect(Collectors.toList());

        Facture facture = new Facture();
        facture.setDate(new Date());
        facture.setMontantTotal(0.0);
        facture.setPrixproduit(0.0);
        facture.setDelaiLivraison(5);
        facture = factureRepo.save(facture);

        ConsultationClient consultation = new ConsultationClient();
        consultation.setClient(client);
        consultation.setDescription(dto.getDescription());
        consultation.setProduitsDemandes(produits);
        consultation.setDateCreation(LocalDateTime.now());
        consultation.setFacture(facture);

        return consultationRepo.save(consultation);
    }

    // Retrieve all consultations (admin)
    public List<ConsultationClient> getAllConsultations() {
        return consultationRepo.findAll();
    }

    // Retrieve consultations by client (employee)
    public Page<ConsultationClient> getConsultationsByClientId(Long clientId, Pageable pageable) {
        return consultationRepo.findByClientId(clientId, pageable);
    }


    // Analyze suppliers based on invoices history
    public List<Map<String, Object>> analyserFournisseursParProduits(List<Long> produitIds) {
        List<Facture> factures = factureRepo.findByProduitsIdIn(produitIds);

        Map<Long, List<Facture>> facturesParFournisseur = factures.stream()
                .collect(Collectors.groupingBy(f -> f.getFournisseur().getId()));

        List<Map<String, Object>> stats = new ArrayList<>();

        for (Map.Entry<Long, List<Facture>> entry : facturesParFournisseur.entrySet()) {
            Long fournisseurId = entry.getKey();
            List<Facture> facturesFournisseur = entry.getValue();

            double prixMoyen = facturesFournisseur.stream()
                    .mapToDouble(Facture::getPrixproduit)
                    .average()
                    .orElse(0.0);

            double delaiMoyen = facturesFournisseur.stream()
                    .mapToInt(Facture::getDelaiLivraison)
                    .average()
                    .orElse(0.0);

            Map<String, Object> fournisseurStats = new HashMap<>();
            fournisseurStats.put("fournisseurId", fournisseurId);
            fournisseurStats.put("prixMoyen", prixMoyen);
            fournisseurStats.put("delaiMoyen", delaiMoyen);

            stats.add(fournisseurStats);
        }

        return stats;
    }

    // ==== Paginated consultation history with optional client filter ====
 // Paginated retrieval of all consultations (admin)
    public Page<ConsultationClient> getAllConsultations(Pageable pageable) {
        return consultationRepo.findAll(pageable);
    }

    public Page<ConsultationHistoryDTO> getConsultationHistory(Long clientId, Pageable pageable) {
        Page<ConsultationClient> page;
        if (clientId != null) {
            page = consultationRepo.findByClientId(clientId, pageable);
        } else {
            page = consultationRepo.findAll(pageable);
        }

        return page.map(c -> {
            LocalDateTime factureDate = null;
            Double factureMontant = null;
            String factureDevise = null;

            if (c.getFacture() != null) {
                factureDate = c.getFacture().getDate() != null
                        ? LocalDateTime.ofInstant(c.getFacture().getDate().toInstant(), ZoneId.systemDefault())
                        : null;
                factureMontant = c.getFacture().getMontantTotal();
                factureDevise = c.getFacture().getDevise();
            }

            return new ConsultationHistoryDTO(
                    c.getId(),
                    c.getClient().getNom(),
                    c.getDescription(),
                    c.getProduitsDemandes().stream().map(Produit::getNom).collect(Collectors.toList()),
                    c.getDateCreation(),
                    factureDate,
                    factureMontant,
                    factureDevise
            );
        });
    }
}
