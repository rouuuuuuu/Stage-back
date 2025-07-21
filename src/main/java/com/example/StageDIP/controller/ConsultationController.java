package com.example.StageDIP.controller;

import com.example.StageDIP.dto.ConsultationClientDTO;
import com.example.StageDIP.model.ConsultationClient;
import com.example.StageDIP.service.ConsultationService;

import jakarta.validation.Valid;

import java.util.List;

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
    public ResponseEntity<?> createConsultation(@Valid @RequestBody ConsultationClientDTO dto) {
        try {
            ConsultationClient result = consultationService.createConsultation(dto);
            return ResponseEntity.status(201).body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getConsultations(@RequestParam(required = false) Long clientId) {
        try {
            List<ConsultationClient> consultations;
            if (clientId != null) {
                consultations = consultationService.getConsultationsByClientId(clientId);
            } else {
                consultations = consultationService.getAllConsultations();
            }
            return ResponseEntity.ok(consultations);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur : impossible de récupérer les consultations");
        }
    }
}
