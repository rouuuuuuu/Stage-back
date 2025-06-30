package com.example.StageDIP.controller;

import com.example.StageDIP.model.Fournisseur;
import com.example.StageDIP.service.FournisseurService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

@RestController
@RequestMapping("/api/fournisseurs")
public class FournisseurController {
    private final FournisseurService service;

    public FournisseurController(FournisseurService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<Fournisseur>> getAll(Pageable pageable) {
        Page<Fournisseur> fournisseurs = service.getAll(pageable);
        return ResponseEntity.ok(fournisseurs);
    }

    @PostMapping
    public Fournisseur add(@RequestBody Fournisseur f) {
        return service.save(f);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fournisseur> updateFournisseur(@PathVariable Long id, @RequestBody Fournisseur fournisseur) {
        Fournisseur updated = service.update(id, fournisseur);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<Fournisseur>> filterFournisseurs(
        @RequestParam(required = false) Double minPrix,
        @RequestParam(required = false) Double maxPrix,
        @RequestParam(required = false) Double minNotation,
        @RequestParam(required = false) String categorie,
        @RequestParam(required = false) String nomProduit,
        @RequestParam(required = false) String devise,
        @RequestParam(required = false) Integer maxDelai,
        Pageable pageable
    ) {
        Page<Fournisseur> fournisseurs = service.filterFournisseurs(
            minPrix, maxPrix, minNotation, categorie, nomProduit, devise, maxDelai, pageable
        );
        return ResponseEntity.ok(fournisseurs);
    }
}