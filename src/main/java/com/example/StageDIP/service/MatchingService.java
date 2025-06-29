package com.example.StageDIP.service;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.StageDIP.dto.FournisseurMatchDTO;
import com.example.StageDIP.model.ConsultationClient;
import com.example.StageDIP.model.Produit;
import com.example.StageDIP.repository.FournisseurRepository;
import com.example.StageDIP.repository.ProduitRepository;


@Service
public class MatchingService {

    private final FournisseurRepository fournisseurRepository;
    private final ProduitRepository produitRepository;

    public MatchingService(FournisseurRepository fournisseurRepository, ProduitRepository produitRepository) {
        this.fournisseurRepository = fournisseurRepository;
        this.produitRepository = produitRepository;
    }

    public List<FournisseurMatchDTO> matchFournisseurs(ConsultationClient consultation) {
        List<Produit> produitsDemandes = consultation.getProduitsDemandes();

        return fournisseurRepository.findAll().stream()
            .map(fournisseur -> {
                List<Produit> produitsFournis = produitRepository.findByFournisseur(fournisseur);
                double prixTotal = 0;
                int produitsTrouvés = 0;

                for (Produit produitDemande : produitsDemandes) {
                    for (Produit fourni : produitsFournis) {
                        if (fourni.getNom().equalsIgnoreCase(produitDemande.getNom())) {
                            prixTotal += fourni.getPrixUnitaire();
                            produitsTrouvés++;
                        }
                    }
                }

                double couverture = (double) produitsTrouvés / produitsDemandes.size();
                return new FournisseurMatchDTO(fournisseur, prixTotal, couverture);
            })
            .filter(dto -> dto.getCouverture() > 0)
            .sorted(Comparator.comparingDouble(FournisseurMatchDTO::getPrixTotal))
            .collect(Collectors.toList());
    }
}
