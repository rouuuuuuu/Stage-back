package com.example.StageDIP.controller;

import com.example.StageDIP.model.ConsultationClient;
import com.example.StageDIP.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationClientService;

    @PostMapping("/client/{clientId}")
    public ResponseEntity<ConsultationClient> addConsultationToClient(
            @PathVariable Long clientId,
            @RequestBody ConsultationClient consultationClient) {

        ConsultationClient saved = consultationClientService.addConsultation(clientId, consultationClient);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<Page<ConsultationClient>> getClientConsultations(
            @PathVariable Long clientId,
            Pageable pageable) {

        return ResponseEntity.ok(consultationClientService.getConsultationsByClientId(clientId, pageable));
    }

    @GetMapping
    public ResponseEntity<Page<ConsultationClient>> getAllConsultations(Pageable pageable) {
        return ResponseEntity.ok(consultationClientService.getAllConsultations(pageable));
    }
}
