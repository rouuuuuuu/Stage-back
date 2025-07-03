package com.example.StageDIP.controller;

import com.example.StageDIP.dto.FactureDTO;
import com.example.StageDIP.model.Facture;
import com.example.StageDIP.service.FactureService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> add(@RequestBody FactureDTO dto) {
        try {
            Facture saved = service.saveFromDTO(dto);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.ok("Facture supprimée avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Facture introuvable avec ID: " + id);
        }
    }
}
