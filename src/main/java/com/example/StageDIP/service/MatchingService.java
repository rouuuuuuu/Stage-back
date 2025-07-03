package com.example.StageDIP.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.StageDIP.dto.FournisseurMatchDTO;
import com.example.StageDIP.dto.MatchWeightsDTO;
import com.example.StageDIP.model.Facture;
import com.example.StageDIP.model.Fournisseur;
import com.example.StageDIP.model.Produit;
import com.example.StageDIP.repository.FactureRepository;
import com.example.StageDIP.repository.FournisseurRepository;
import com.example.StageDIP.repository.ProduitRepository;

@Service
public class MatchingService {

    private final FournisseurRepository fournisseurRepository;
    private final ProduitRepository produitRepository;
    private final FactureRepository factureRepository;

    // Proper injection of all repositories - NO MORE NULLS
    public MatchingService(FournisseurRepository fournisseurRepository, 
                           ProduitRepository produitRepository,
                           FactureRepository factureRepository) {
        this.fournisseurRepository = fournisseurRepository;
        this.produitRepository = produitRepository;
        this.factureRepository = factureRepository;
    }

    public List<FournisseurMatchDTO> matchFournisseurs(List<Long> produitIds, MatchWeightsDTO poids) {
        if (produitIds == null || produitIds.isEmpty()) {
            // Defensive: if no products requested, return empty list
            return Collections.emptyList();
        }

        List<Fournisseur> fournisseurs = fournisseurRepository.findAll();
        List<Produit> produitsDemandes = produitRepository.findAllById(produitIds);
        Set<Long> produitsDemandesIds = produitsDemandes.stream()
                                                        .map(Produit::getId)
                                                        .collect(Collectors.toSet());

        // Fetch all products of these suppliers
        List<Produit> produitsFournisseurs = produitRepository.findByFournisseurIn(fournisseurs);

        // Group products by fournisseur
        Map<Long, List<Produit>> produitsParFournisseur = produitsFournisseurs.stream()
            .collect(Collectors.groupingBy(p -> p.getFournisseur().getId()));

        // Compute min/max values for normalization
        double maxPrix = Double.MIN_VALUE;
        double minPrix = Double.MAX_VALUE;
        double maxDelai = Double.MIN_VALUE;
        double minDelai = Double.MAX_VALUE;
        double maxNotation = 5.0;  // configurable if needed
        double minNotation = 0.0;

        // Temporary map to hold stats per fournisseur
        Map<Long, FournisseurStats> statsMap = new HashMap<>();

        // First pass: calculate stats per fournisseur and track min/max for price and delay
        for (Fournisseur f : fournisseurs) {
            List<Produit> produitsF = produitsParFournisseur.getOrDefault(f.getId(), Collections.emptyList());

            // Products that match the requested ones
            List<Produit> produitsCommuns = produitsF.stream()
                .filter(p -> produitsDemandesIds.contains(p.getId()))
                .collect(Collectors.toList());

            if (produitsCommuns.isEmpty()) {
                // Supplier doesn't offer requested products, skip scoring but keep stats empty
                statsMap.put(f.getId(), new FournisseurStats(0.0, Double.MAX_VALUE, f.getNotation() != null ? f.getNotation() : minNotation, 0));
                continue;
            }

            double prixMoyen = produitsCommuns.stream()
                                .mapToDouble(Produit::getPrixUnitaire)
                                .average()
                                .orElse(0.0);

            List<Facture> facturesF = factureRepository.findByFournisseur(f);
            double delaiMoyen = facturesF.isEmpty() ? Double.MAX_VALUE : facturesF.stream()
                .mapToInt(Facture::getDelaiLivraison)
                .average()
                .orElse(Double.MAX_VALUE);

            // Update min/max for price
            if (prixMoyen > maxPrix) maxPrix = prixMoyen;
            if (prixMoyen < minPrix) minPrix = prixMoyen;

            // Update min/max for delay
            if (delaiMoyen > maxDelai) maxDelai = delaiMoyen;
            if (delaiMoyen < minDelai) minDelai = delaiMoyen;

            double notation = f.getNotation() != null ? f.getNotation() : minNotation;

            statsMap.put(f.getId(), new FournisseurStats(prixMoyen, delaiMoyen, notation, produitsCommuns.size()));
        }

        // Epsilon to avoid division by zero
        double epsilon = 1e-6;

        // Second pass: calculate normalized scores and create DTOs
        List<FournisseurMatchDTO> results = new ArrayList<>();

        for (Fournisseur f : fournisseurs) {
            FournisseurStats s = statsMap.get(f.getId());

            // If supplier has no matching products, skip scoring (or assign zero)
            if (s.nbProduitsCommun == 0) {
                continue;
            }

            // Normalize price (lower price better => inverted scale)
            double prixScore = (maxPrix - minPrix) < epsilon ? 1.0 : (maxPrix - s.prixMoyen) / (maxPrix - minPrix);

            // Normalize delay (lower delay better => inverted scale)
            double delaiScore = (maxDelai - minDelai) < epsilon ? 1.0 : (maxDelai - s.delaiMoyen) / (maxDelai - minDelai);

            // Normalize notation (higher better)
            double notationScore = (maxNotation - minNotation) < epsilon ? 1.0 : (s.notation - minNotation) / (maxNotation - minNotation);

            // Coverage: fraction of requested products supplier offers
            double couverture = produitIds.isEmpty() ? 0.0 : (double) s.nbProduitsCommun / produitIds.size();

            // Weighted score
            double scorePondere = prixScore * poids.getPoidsPrix()
                                + delaiScore * poids.getPoidsDelai()
                                + notationScore * poids.getPoidsNotation();

            // Final score adds coverage as bonus (you can tweak to multiply if needed)
            double scoreFinal = scorePondere + couverture;

            FournisseurMatchDTO dto = new FournisseurMatchDTO(f, s.prixMoyen, couverture);
            dto.setScore(scoreFinal);
            dto.setMinDelai((int) Math.round(s.delaiMoyen));
            dto.setNotation(s.notation);

            results.add(dto);
        }

        // Sort by descending score
        results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        return results;
    }

    // Helper class for stats
    private static class FournisseurStats {
        double prixMoyen;
        double delaiMoyen;
        double notation;
        int nbProduitsCommun;

        FournisseurStats(double prixMoyen, double delaiMoyen, double notation, int nbProduitsCommun) {
            this.prixMoyen = prixMoyen;
            this.delaiMoyen = delaiMoyen;
            this.notation = notation;
            this.nbProduitsCommun = nbProduitsCommun;
        }
    }
}
