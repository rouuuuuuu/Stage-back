package com.example.StageDIP.controller;

import com.example.StageDIP.dto.ConsultationClientDTO;
import com.example.StageDIP.dto.ConsultationHistoryDTO;
import com.example.StageDIP.model.ConsultationClient;
import com.example.StageDIP.service.ConsultationService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    
    @GetMapping("/history")
    public ResponseEntity<?> getConsultationHistory(
            @RequestParam(required = false) Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ConsultationHistoryDTO> historyPage = consultationService.getConsultationHistory(clientId, pageable);
            return ResponseEntity.ok(historyPage);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur : impossible de récupérer l'historique");
        }
    }

    @GetMapping
    public ResponseEntity<?> getConsultations(
            @RequestParam(required = false) Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            if (clientId != null) {
                Page<ConsultationClient> consultationsPage = consultationService.getConsultationsByClientId(clientId, pageable);
                return ResponseEntity.ok(consultationsPage);
            } else {
                Page<ConsultationClient> consultationsPage = consultationService.getAllConsultations(pageable);
                return ResponseEntity.ok(consultationsPage);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur : impossible de récupérer les consultations");
        }
    }
}
