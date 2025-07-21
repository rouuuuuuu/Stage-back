package com.example.StageDIP.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.StageDIP.repository.FournisseurSpecifications;
import org.springframework.data.jpa.domain.Specification;
import com.example.StageDIP.model.Fournisseur;
import com.example.StageDIP.repository.FournisseurRepository;
import org.springframework.cache.annotation.Cacheable;

@Service
public class FournisseurService {

    private final FournisseurRepository repo;

    public FournisseurService(FournisseurRepository repo) {
        this.repo = repo;
    }

    public Page<Fournisseur> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Fournisseur save(Fournisseur f) {
        return repo.save(f);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Page<Fournisseur> filterFournisseurs(
            Double minMontantTotalDernier, Double maxMontantTotalDernier,
            Double minNotation,
            String categorie, String nomProduit,
            String devise,
            Integer maxDelai,
            Pageable pageable) {

        Specification<Fournisseur> spec = FournisseurSpecifications.filter(
                minMontantTotalDernier,
                maxMontantTotalDernier,
                minNotation,
                categorie,
                nomProduit,
                devise,
                maxDelai
        );

        return repo.findAll(spec, pageable);
    }
    @Cacheable("fournisseurs")
    public List<Fournisseur> getAllWithDetails() {
        return repo.findAllWithDetails();
    }

    public Fournisseur update(Long id, Fournisseur fournisseurData) {
        Optional<Fournisseur> optional = repo.findById(id);
        if (optional.isEmpty()) return null;

        Fournisseur existing = optional.get();
        existing.setNom(fournisseurData.getNom());
        existing.setAdresse(fournisseurData.getAdresse());
        existing.setEmail(fournisseurData.getEmail());
        existing.setNotation(fournisseurData.getNotation());

        return repo.save(existing);
    }

    @Cacheable("fournisseurs")
    public List<Fournisseur> getAllFlat() {
        return repo.findAll(); 
    }
}
