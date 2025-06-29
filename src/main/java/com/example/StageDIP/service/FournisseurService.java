package com.example.StageDIP.service;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.StageDIP.model.Fournisseur;

import com.example.StageDIP.repository.FournisseurRepository;
import com.example.StageDIP.repository.FournisseurSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
public class FournisseurService {

    private final FournisseurRepository repo;

    public FournisseurService(FournisseurRepository repo) {
        this.repo = repo;
    }

    public List<Fournisseur> getAll() {
        return repo.findAll();
    }

    public Fournisseur save(Fournisseur f) {
        return repo.save(f);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }


    public Page<Fournisseur> filterFournisseurs(
    	    Double minPrix, Double maxPrix, Double minNotation,
    	    String categorie, String nomProduit, String devise,
    	    Integer maxDelai, Pageable pageable) {

    	    Specification<Fournisseur> spec = Specification.where(null);

    	    spec = spec.and(FournisseurSpecifications.prixProduitBetween(minPrix, maxPrix));
    	    spec = spec.and(FournisseurSpecifications.notationMin(minNotation));
    	    spec = spec.and(FournisseurSpecifications.categorieProduitEgale(categorie));
    	    spec = spec.and(FournisseurSpecifications.nomProduitContient(nomProduit));
    	    spec = spec.and(FournisseurSpecifications.deviseFactureEgale(devise));
    	    spec = spec.and(FournisseurSpecifications.delaiLivraisonMax(maxDelai));

    	    return repo.findAll(spec, pageable);
    	}

}
