package com.example.StageDIP.controller;

import com.example.StageDIP.dto.ConsultationClientDTO;
import com.example.StageDIP.model.ConsultationClient;
import com.example.StageDIP.service.ConsultationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @PostMapping
    public ResponseEntity<?> createConsultation(@RequestBody ConsultationClientDTO dto) {
        try {
            ConsultationClient result = consultationService.createConsultation(dto);
            return ResponseEntity.status(201).body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Add this GET mapping right here — no excuses
    @GetMapping
    public ResponseEntity<?> getAllConsultations() {
        try {
            var consultations = consultationService.getAllConsultations();
            return ResponseEntity.ok(consultations);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur : impossible de récupérer les consultations");
        }
    }

}
