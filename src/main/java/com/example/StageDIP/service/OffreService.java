package com.example.StageDIP.service;

import com.example.StageDIP.dto.OffreDTO;
import com.example.StageDIP.model.Fournisseur;
import com.example.StageDIP.model.Offre;
import com.example.StageDIP.repository.FournisseurRepository;
import com.example.StageDIP.repository.OffreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OffreService {

    private final OffreRepository repo;
    private final FournisseurRepository fournisseurRepo;

    public OffreService(OffreRepository repo, FournisseurRepository fournisseurRepo) {
        this.repo = repo;
        this.fournisseurRepo = fournisseurRepo;
    }

    public List<Offre> getAll() {
        return repo.findAll();
    }

    public Optional<Offre> getById(Long id) {
        return repo.findById(id);
    }

    public Offre saveFromDTO(OffreDTO dto) {
        Fournisseur fournisseur = fournisseurRepo.findById(dto.getFournisseurId())
                .orElseThrow(() -> new IllegalArgumentException("Fournisseur introuvable avec ID: " + dto.getFournisseurId()));

        Offre offre = new Offre();
        offre.setDateoffre(dto.getDateoffre());
        offre.setDatevalidite(dto.getDatevalidite());
        offre.setStatut(dto.getStatut());
        offre.setFournisseur(fournisseur);
        offre.setIdfour(dto.getFournisseurId());

        return repo.save(offre);
    }

    public Offre updateFromDTO(Long id, OffreDTO dto) {
        Optional<Offre> optional = repo.findById(id);
        if (optional.isEmpty()) return null;

        Fournisseur fournisseur = fournisseurRepo.findById(dto.getFournisseurId())
                .orElseThrow(() -> new IllegalArgumentException("Fournisseur introuvable avec ID: " + dto.getFournisseurId()));

        Offre offre = optional.get();
        offre.setDateoffre(dto.getDateoffre());
        offre.setDatevalidite(dto.getDatevalidite());
        offre.setStatut(dto.getStatut());
        offre.setFournisseur(fournisseur);
        offre.setIdfour(dto.getFournisseurId());

        return repo.save(offre);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
