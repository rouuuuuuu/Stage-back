package com.example.StageDIP.service;

import com.example.StageDIP.dto.ConsultationClientDTO;
import com.example.StageDIP.model.Client;
import com.example.StageDIP.model.ConsultationClient;
import com.example.StageDIP.model.Produit;
import com.example.StageDIP.repository.ConsultationClientRepository;
import com.example.StageDIP.repository.ProduitRepository;
import com.example.StageDIP.repository.ClientRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsultationServiceTest {

    @Mock
    private ConsultationClientRepository consultationRepo;

    @Mock
    private ProduitRepository produitRepo;
    
    @Mock
    private ClientRepository clientRepo;   // <----- ADD THIS MOCK


    @InjectMocks
    private ConsultationService consultationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createConsultation_ValidClientAndProducts_Success() {
        // Prepare DTO with clientId (Long), not Client object
        ConsultationClientDTO dto = new ConsultationClientDTO();
        dto.setClientId(1L);
        dto.setDescription("Test demande");
        dto.setProduitsIds(List.of(1L, 2L));

        // Mock produit repo
        Produit produit1 = new Produit();
        produit1.setId(1L);
        Produit produit2 = new Produit();
        produit2.setId(2L);

        when(produitRepo.findById(1L)).thenReturn(Optional.of(produit1));
        when(produitRepo.findById(2L)).thenReturn(Optional.of(produit2));
        when(consultationRepo.save(any(ConsultationClient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock client repo inside your service! (You need to mock clientRepo too)
        Client client = new Client();
        client.setId(1L);
        // You need a ClientRepository mock here, add @Mock private ClientRepository clientRepo;
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));

        // Call service
        ConsultationClient result = consultationService.createConsultation(dto);

        assertNotNull(result);
        assertEquals(client, result.getClient());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(2, result.getProduitsDemandes().size());
        verify(consultationRepo, times(1)).save(any(ConsultationClient.class));
    }

    @Test
    void createConsultation_ProductNotFound_ThrowsException() {
        ConsultationClientDTO dto = new ConsultationClientDTO();
        dto.setClientId(1L);
        dto.setDescription("Test produit manquant");
        dto.setProduitsIds(List.of(999L));

        when(produitRepo.findById(999L)).thenReturn(Optional.empty());

        // Mock clientRepo too if service uses it
        Client client = new Client();
        client.setId(1L);
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            consultationService.createConsultation(dto);
        });

        assertEquals("Produit introuvable avec id : 999", ex.getMessage());
        verify(consultationRepo, never()).save(any());
    }
}
