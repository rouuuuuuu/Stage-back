package com.example.StageDIP.service;


import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.StageDIP.dto.FournisseurMatchDTO;
import com.example.StageDIP.dto.MatchWeightsDTO;
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

    public List<FournisseurMatchDTO> matchFournisseurs(
            ConsultationClient consultation, MatchWeightsDTO weights) {

        List<Produit> produitsDemandes = consultation.getProduitsDemandes();
        if (produitsDemandes.isEmpty()) return List.of();

        // Map product names demanded for O(1) lookup
        Set<String> demandedNames = produitsDemandes.stream()
            .map(p -> p.getNom().toLowerCase())
            .collect(Collectors.toSet());

        List<FournisseurMatchDTO> rawScores = fournisseurRepository.findAll().stream()
            .map(fournisseur -> {
                List<Produit> produitsFournis = produitRepository.findByFournisseur(fournisseur);

                // Filter produits fournis matching demandes by name
                List<Produit> matchingProduits = produitsFournis.stream()
                    .filter(p -> demandedNames.contains(p.getNom().toLowerCase()))
                    .collect(Collectors.toList());

                if (matchingProduits.isEmpty()) return null;

                double prixTotal = matchingProduits.stream()
                    .mapToDouble(Produit::getPrixUnitaire)
                    .sum();

                double couverture = (double) matchingProduits.size() / produitsDemandes.size();

                int minDelai = fournisseur.getFactures().stream()
                        .mapToInt(f -> f.getDelaiLivraison())
                        .min()
                        .orElse(1000);

                double notation = fournisseur.getNotation() != null ? fournisseur.getNotation() : 0;

                FournisseurMatchDTO dto = new FournisseurMatchDTO(fournisseur, prixTotal, couverture);
                dto.setScore(0); // placeholder, to be set after normalization
                dto.setMinDelai(minDelai);  // if you add this field or keep in mind
                dto.setNotation(notation);  // same here, consider extending DTO

                return dto;
            })
            .filter(dto -> dto != null)
            .collect(Collectors.toList());

        if (rawScores.isEmpty()) return List.of();

        // Get min and max for price and delay to normalize
        double minPrix = rawScores.stream().mapToDouble(FournisseurMatchDTO::getPrixTotal).min().orElse(0);
        double maxPrix = rawScores.stream().mapToDouble(FournisseurMatchDTO::getPrixTotal).max().orElse(1);
        int minDelaiGlobal = rawScores.stream().mapToInt(dto -> dto.getMinDelai()).min().orElse(0);
        int maxDelaiGlobal = rawScores.stream().mapToInt(dto -> dto.getMinDelai()).max().orElse(1);
        double minNotation = 0;
        double maxNotation = 5; // assuming scale 0-5

        // Normalize and compute score
        for (FournisseurMatchDTO dto : rawScores) {
            double normPrix = (maxPrix - dto.getPrixTotal()) / (maxPrix - minPrix + 1e-6);
            double normDelai = (maxDelaiGlobal - dto.getMinDelai()) / (double)(maxDelaiGlobal - minDelaiGlobal + 1e-6);
            double normNotation = dto.getNotation() / maxNotation;

            double score = normPrix * weights.getPoidsPrix()
                         + normDelai * weights.getPoidsDelai()
                         + normNotation * weights.getPoidsNotation();

            // Optionally multiply by couverture to favor suppliers that cover more
            score *= dto.getCouverture();

            dto.setScore(score);
        }

        return rawScores.stream()
            .sorted(Comparator.comparingDouble(FournisseurMatchDTO::getScore).reversed())
            .collect(Collectors.toList());
    }
}