package com.example.StageDIP.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.StageDIP.controller.ConsultationController;
import com.example.StageDIP.model.ConsultationClient;
import com.example.StageDIP.model.ConsultationClientDTO;
import com.example.StageDIP.service.ConsultationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ConsultationController.class)
public class ConsultationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultationService consultationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createConsultation_ValidData_ReturnsCreated() throws Exception {
        ConsultationClientDTO dto = new ConsultationClientDTO();
        dto.setClientId(1L);
        dto.setDescription("Demande de produits");
        dto.setProduitsIds(Arrays.asList(1L, 2L));

        ConsultationClient response = new ConsultationClient();
        response.setClientId(dto.getClientId());
        response.setDescription(dto.getDescription());

        when(consultationService.createConsultation(any())).thenReturn(response);

        mockMvc.perform(post("/api/consultations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void createConsultation_InvalidProduct_ReturnsBadRequest() throws Exception {
        ConsultationClientDTO dto = new ConsultationClientDTO();
        dto.setClientId(1L);
        dto.setDescription("Demande cassée");
        dto.setProduitsIds(Arrays.asList(999L));

        when(consultationService.createConsultation(any()))
                .thenThrow(new IllegalArgumentException("Produit introuvable"));

        mockMvc.perform(post("/api/consultations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
