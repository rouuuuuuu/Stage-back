package com.example.StageDIP.service;

import com.example.StageDIP.model.Produit;
import com.example.StageDIP.repository.ProduitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProduitService {

    private final ProduitRepository repo;

    public ProduitService(ProduitRepository repo) {
        this.repo = repo;
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
