package com.example.StageDIP.controller;

import com.example.StageDIP.dto.ProduitDTO;
import com.example.StageDIP.model.Produit;
import com.example.StageDIP.service.ProduitService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {

    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @GetMapping
    public ResponseEntity<Page<ProduitDTO>> getAllProduits(Pageable pageable) {
        Page<ProduitDTO> page = produitService.getAllProduits(pageable)
                .map(ProduitDTO::new);
        return ResponseEntity.ok(page);
    }

    @PostMapping
    public Produit add(@Valid @RequestBody Produit produit) {
        return produitService.save(produit);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        produitService.delete(id);
    }

    @GetMapping("/search")
    public List<Produit> searchProduits(@RequestParam String query) {
        return produitService.searchProduits(query);
    }

    @PostMapping("/nouveau")
    public ResponseEntity<Produit> addNewProduit(@Valid @RequestBody Produit produit) {
        Produit savedProduit = produitService.addNewProduct(produit);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduit);
    }
}
