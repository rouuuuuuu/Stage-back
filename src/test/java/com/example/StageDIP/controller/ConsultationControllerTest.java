package  com.example.StageDIP.controller;

import com.example.StageDIP.model.ConsultationClientDTO;
import com.example.StageDIP.model.ConsultationClient;
import com.example.StageDIP.model.Produit;
import com.example.StageDIP.repository.ConsultationRepository;
import com.example.StageDIP.repository.ProduitRepository;
import com.example.StageDIP.service.ConsultationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class ConsultationServiceTest {

 @Mock
 private ConsultationClientRepository consultationRepository;

 @Mock
 private ProduitRepository produitRepository;

 @InjectMocks
 private ConsultationService consultationService;

 @Test
 void createConsultation_WithValidData_ShouldReturnConsultation() {
     // Arrange
     ConsultationClientDTO dto = new ConsultationClientDTO();
     dto.setClientId(1L);
     dto.setDescription("Besoin de matériel informatique");
     dto.setProduitsIds(Arrays.asList(1L, 2L));

     Produit produit1 = new Produit();
     produit1.setId(1L);
     Produit produit2 = new Produit();
     produit2.setId(2L);

     when(produitRepository.findAllById(any())).thenReturn(Arrays.asList(produit1, produit2));
     when(consultationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

     // Act
     ConsultationClient result = consultationService.createConsultation(dto);

     // Assert
     assertNotNull(result);
     assertEquals(dto.getClientId(), result.getClientId());
     assertEquals(dto.getDescription(), result.getDescription());
     assertEquals(2, result.getProduitsDemandes().size());
 }

 @Test
 void createConsultation_WithInvalidProduct_ShouldThrowException() {
     // Arrange
     ConsultationClientDTO dto = new ConsultationClientDTO();
     dto.setProduitsIds(Arrays.asList(99L)); // ID inexistant

     when(produitRepository.findAllById(any())).thenReturn(List.of());

     // Act & Assert
     assertThrows(IllegalArgumentException.class, () -> {
         consultationService.createConsultation(dto);
     });
 }
}

