package com.example.StageDIP.service;

import com.example.StageDIP.model.Fournisseur;
import com.example.StageDIP.repository.FournisseurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
