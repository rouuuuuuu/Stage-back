package com.example.StageDIP.service;

import com.example.StageDIP.model.Produit;
import com.example.StageDIP.repository.ProduitRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProduitService {

    private final ProduitRepository repo;

    public ProduitService(ProduitRepository produitRepository) {
        this.repo = produitRepository;
    }

    public Page<Produit> getAllProduits(Pageable pageable) {
        return repo.findAll(pageable);
    }
    public List<Produit> getAll() {
        return repo.findAll();
    }

    public Produit save(Produit p) {
        return repo.save(p);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<Produit> searchProduits(String query) {
        return repo.searchByNomPartiel(query)
                   .stream()
                   .limit(10)
                   .toList();
    }
    public Produit addNewProduct(Produit produit) {
        return repo.save(produit);
    }


}
