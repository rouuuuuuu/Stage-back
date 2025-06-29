package com.example.StageDIP.controller;

import com.example.StageDIP.dto.FournisseurMatchDTO;
import com.example.StageDIP.model.ConsultationClient;
import com.example.StageDIP.repository.ConsultationClientRepository;
import com.example.StageDIP.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    private final ConsultationClientRepository consultationRepository;
    private final MatchingService matchingService;

    public MatchingController(ConsultationClientRepository consultationRepository, MatchingService matchingService) {
        this.consultationRepository = consultationRepository;
        this.matchingService = matchingService;
    }

    @GetMapping("/{idConsultation}")
    public ResponseEntity<List<FournisseurMatchDTO>> getMatching(@PathVariable Long idConsultation) {
        Optional<ConsultationClient> consultationOpt = consultationRepository.findById(idConsultation);
        if (consultationOpt.isEmpty()) return ResponseEntity.notFound().build();

        List<FournisseurMatchDTO> matches = matchingService.matchFournisseurs(consultationOpt.get());
        return ResponseEntity.ok(matches);
    }
}
