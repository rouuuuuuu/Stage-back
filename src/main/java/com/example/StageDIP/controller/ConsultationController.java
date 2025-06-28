package com.example.StageDIP.controller;

import com.example.StageDIP.model.ConsultationClientDTO;
import com.example.StageDIP.model.ConsultationClient;
import com.example.StageDIP.service.ConsultationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consultations")
public class ClientController {

    private final ConsultationService consultationService;

    public ClientController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @PostMapping
    public ResponseEntity<ConsultationClient> createConsultation(
            @Valid @RequestBody ConsultationClientDTO consultationDTO) {
        ConsultationClient createdConsultation = consultationService.createConsultation(consultationDTO);
        return new ResponseEntity<>(createdConsultation, HttpStatus.CREATED);
    }
}