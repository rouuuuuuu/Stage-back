package com.example.StageDIP.service;

import com.example.StageDIP.dto.FactureDTO;
import com.example.StageDIP.model.Facture;
import com.example.StageDIP.model.Fournisseur;
import com.example.StageDIP.model.Produit;
import com.example.StageDIP.repository.FactureRepository;
import com.example.StageDIP.repository.FournisseurRepository;
import com.example.StageDIP.repository.ProduitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FactureService {

    private final FactureRepository repo;
    private final ProduitRepository produitRepo;
    private final FournisseurRepository fournisseurRepo;

    public FactureService(FactureRepository repo, ProduitRepository produitRepo, FournisseurRepository fournisseurRepo) {
        this.repo = repo;
        this.produitRepo = produitRepo;
        this.fournisseurRepo = fournisseurRepo;
    }

    public List<Facture> getAll() {
        return repo.findAll();
    }

    public Facture save(Facture f) {
        return repo.save(f);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Facture saveFromDTO(FactureDTO dto) {
        Fournisseur fournisseur = fournisseurRepo.findById(dto.getFournisseurId())
                .orElseThrow(() -> new IllegalArgumentException("Fournisseur introuvable avec ID: " + dto.getFournisseurId()));

        Set<Produit> produits = dto.getProduitIds().stream()
                .map(id -> produitRepo.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Produit introuvable avec ID: " + id)))
                .collect(Collectors.toSet());

        Facture facture = new Facture(dto.getDate(), dto.getMontantTotal(), dto.getDelaiLivraison(), fournisseur, dto.getPrixproduit());

        produits.forEach(p -> p.setFacture(facture));
        facture.setProduits(produits);

        return repo.save(facture);
    }
}
