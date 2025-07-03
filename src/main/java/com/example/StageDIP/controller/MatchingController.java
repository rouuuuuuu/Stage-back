package com.example.StageDIP.controller;

import com.example.StageDIP.dto.FournisseurMatchDTO;
import com.example.StageDIP.dto.MatchWeightsDTO;
import com.example.StageDIP.model.ConsultationClient;
import com.example.StageDIP.repository.ConsultationClientRepository;
import com.example.StageDIP.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    private final ConsultationClientRepository consultationRepository;
    private final MatchingService matchingService;

    public MatchingController(ConsultationClientRepository consultationRepository, MatchingService matchingService) {
        this.consultationRepository = consultationRepository;
        this.matchingService = matchingService;
    }

    // Accept weights as optional query params with defaults
    @GetMapping("/{idConsultation}")
    public ResponseEntity<List<FournisseurMatchDTO>> getMatching(
        @PathVariable Long idConsultation,
        @RequestParam(required = false, defaultValue = "0.4") double poidsPrix,
        @RequestParam(required = false, defaultValue = "0.3") double poidsDelai,
        @RequestParam(required = false, defaultValue = "0.3") double poidsNotation) {

        Optional<ConsultationClient> consultationOpt = consultationRepository.findById(idConsultation);
        if (consultationOpt.isEmpty()) return ResponseEntity.notFound().build();

        ConsultationClient consultation = consultationOpt.get();

        // Extract product IDs from consultation
        List<Long> produitIds = consultation.getProduitsDemandes()
                                           .stream()
                                           .map(p -> p.getId())
                                           .collect(Collectors.toList());

        MatchWeightsDTO weights = new MatchWeightsDTO(poidsPrix, poidsDelai, poidsNotation);
        List<FournisseurMatchDTO> matches = matchingService.matchFournisseurs(produitIds, weights);

        return ResponseEntity.ok(matches);
    }
}