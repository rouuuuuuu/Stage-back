package com.example.StageDIP.controller;

import com.example.StageDIP.model.Produit;
import com.example.StageDIP.service.ProduitService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {

    private final ProduitService service;

    public ProduitController(ProduitService service) {
        this.service = service;
    }

    @GetMapping
    public List<Produit> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Produit add(@RequestBody Produit produit) {
        return service.save(produit);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
