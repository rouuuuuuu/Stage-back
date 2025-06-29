package com.example.StageDIP.controller;

import com.example.StageDIP.model.Fournisseur;
import com.example.StageDIP.service.FournisseurService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fournisseurs")
public class FournisseurController {
    private final FournisseurService service;

    public FournisseurController(FournisseurService service) {
        this.service = service;
    }

    @GetMapping
    public List<Fournisseur> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Fournisseur add(@RequestBody Fournisseur f) {
        return service.save(f);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Fournisseur>> filterFournisseurs(
            @RequestParam(required = false) Double minPrix,
            @RequestParam(required = false) Double maxPrix,
            @RequestParam(required = false) Double minNotation,
            @RequestParam(required = false) String categorie,
            @RequestParam(required = false) String nomProduit,
            @RequestParam(required = false) String devise) {

        List<Fournisseur> fournisseurs = service.filterFournisseurs(minPrix, maxPrix, minNotation, categorie, nomProduit, devise);
        return ResponseEntity.ok(fournisseurs);
    }
}