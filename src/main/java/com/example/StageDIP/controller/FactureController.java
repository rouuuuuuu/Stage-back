package com.example.StageDIP.controller;

import com.example.StageDIP.model.Facture;
import com.example.StageDIP.service.FactureService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factures")
public class FactureController {

    private final FactureService service;

    public FactureController(FactureService service) {
        this.service = service;
    }

    @GetMapping
    public List<Facture> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Facture add(@RequestBody Facture facture) {
        return service.save(facture);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
