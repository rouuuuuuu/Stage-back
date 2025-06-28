package com.example.StageDIP.service;

import com.example.StageDIP.model.Facture;
import com.example.StageDIP.repository.FactureRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FactureService {
    private final FactureRepository repo;

    public FactureService(FactureRepository repo) {
        this.repo = repo;
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
}
