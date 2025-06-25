package com.example.StageDIP.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

import com.example.StageDIP.model.ConsultationClient;
import com.example.StageDIP.model.ConsultationClientDTO;
import com.example.StageDIP.model.Produit;
import com.example.StageDIP.repository.ConsultationRepository;
import com.example.StageDIP.repository.ProduitRepository;

public class ConsultationServiceTest {

    @Test
    void createConsultation_WithValidData_ShouldReturnConsultation() {
        // Arrange
        ConsultationClientDTO dto = new ConsultationClientDTO();
        dto.setClientId(1L);
        dto.setDescription("Besoin de matériel informatique");
        dto.setProduitsIds(Arrays.asList(1L, 2L));

        // Création des mocks manuels
        ProduitRepository produitRepo = new ProduitRepository() {
            @Override
            public List<Produit> findAllById(Iterable<Long> ids) {
                Produit p1 = new Produit(); p1.setId(1L);
                Produit p2 = new Produit(); p2.setId(2L);
                return Arrays.asList(p1, p2);
            }
            
            // Implémentez les autres méthodes nécessaires avec des comportements par défaut
            @Override
            public Optional<Produit> findById(Long id) {
                return Optional.empty();
            }

			@Override
			public void flush() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public <S extends Produit> S saveAndFlush(S entity) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends Produit> List<S> saveAllAndFlush(Iterable<S> entities) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void deleteAllInBatch(Iterable<Produit> entities) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAllByIdInBatch(Iterable<Long> ids) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAllInBatch() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Produit getOne(Long id) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Produit getById(Long id) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Produit getReferenceById(Long id) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends Produit> List<S> findAll(Example<S> example) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends Produit> List<S> findAll(Example<S> example, Sort sort) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends Produit> List<S> saveAll(Iterable<S> entities) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<Produit> findAll() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends Produit> S save(S entity) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean existsById(Long id) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public long count() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void deleteById(Long id) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void delete(Produit entity) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAllById(Iterable<? extends Long> ids) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAll(Iterable<? extends Produit> entities) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAll() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public List<Produit> findAll(Sort sort) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Page<Produit> findAll(Pageable pageable) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends Produit> Optional<S> findOne(Example<S> example) {
				// TODO Auto-generated method stub
				return Optional.empty();
			}

			@Override
			public <S extends Produit> Page<S> findAll(Example<S> example, Pageable pageable) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends Produit> long count(Example<S> example) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public <S extends Produit> boolean exists(Example<S> example) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public <S extends Produit, R> R findBy(Example<S> example,
					Function<FetchableFluentQuery<S>, R> queryFunction) {
				// TODO Auto-generated method stub
				return null;
			}
        };

        ConsultationRepository consultationRepo = new ConsultationRepository() {
            @Override
            public <S extends ConsultationClient> S save(S entity) {
                return entity; // Simplement retourne l'entité telle quelle
            }

			@Override
			public void flush() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public <S extends ConsultationClient> S saveAndFlush(S entity) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends ConsultationClient> List<S> saveAllAndFlush(Iterable<S> entities) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void deleteAllInBatch(Iterable<ConsultationClient> entities) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAllByIdInBatch(Iterable<Long> ids) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAllInBatch() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public ConsultationClient getOne(Long id) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ConsultationClient getById(Long id) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ConsultationClient getReferenceById(Long id) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends ConsultationClient> List<S> findAll(Example<S> example) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends ConsultationClient> List<S> findAll(Example<S> example, Sort sort) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends ConsultationClient> List<S> saveAll(Iterable<S> entities) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<ConsultationClient> findAll() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<ConsultationClient> findAllById(Iterable<Long> ids) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Optional<ConsultationClient> findById(Long id) {
				// TODO Auto-generated method stub
				return Optional.empty();
			}

			@Override
			public boolean existsById(Long id) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public long count() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void deleteById(Long id) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void delete(ConsultationClient entity) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAllById(Iterable<? extends Long> ids) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAll(Iterable<? extends ConsultationClient> entities) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAll() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public List<ConsultationClient> findAll(Sort sort) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Page<ConsultationClient> findAll(Pageable pageable) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends ConsultationClient> Optional<S> findOne(Example<S> example) {
				// TODO Auto-generated method stub
				return Optional.empty();
			}

			@Override
			public <S extends ConsultationClient> Page<S> findAll(Example<S> example, Pageable pageable) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends ConsultationClient> long count(Example<S> example) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public <S extends ConsultationClient> boolean exists(Example<S> example) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public <S extends ConsultationClient, R> R findBy(Example<S> example,
					Function<FetchableFluentQuery<S>, R> queryFunction) {
				// TODO Auto-generated method stub
				return null;
			}
            
            // Implémentez les autres méthodes nécessaires
        };

        ConsultationService service = new ConsultationService(consultationRepo, produitRepo);

        // Act
        ConsultationClient result = service.createConsultation(dto);

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
        dto.setProduitsIds(Arrays.asList(99L));

        ProduitRepository produitRepo = new ProduitRepository() {
            @Override
            public List<Produit> findAllById(Iterable<Long> ids) {
                return List.of(); // Retourne une liste vide pour simuler des IDs invalides
            }

			@Override
			public void flush() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public <S extends Produit> S saveAndFlush(S entity) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends Produit> List<S> saveAllAndFlush(Iterable<S> entities) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void deleteAllInBatch(Iterable<Produit> entities) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAllByIdInBatch(Iterable<Long> ids) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAllInBatch() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Produit getOne(Long id) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Produit getById(Long id) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Produit getReferenceById(Long id) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends Produit> List<S> findAll(Example<S> example) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends Produit> List<S> findAll(Example<S> example, Sort sort) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends Produit> List<S> saveAll(Iterable<S> entities) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<Produit> findAll() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends Produit> S save(S entity) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Optional<Produit> findById(Long id) {
				// TODO Auto-generated method stub
				return Optional.empty();
			}

			@Override
			public boolean existsById(Long id) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public long count() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void deleteById(Long id) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void delete(Produit entity) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAllById(Iterable<? extends Long> ids) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAll(Iterable<? extends Produit> entities) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAll() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public List<Produit> findAll(Sort sort) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Page<Produit> findAll(Pageable pageable) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends Produit> Optional<S> findOne(Example<S> example) {
				// TODO Auto-generated method stub
				return Optional.empty();
			}

			@Override
			public <S extends Produit> Page<S> findAll(Example<S> example, Pageable pageable) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends Produit> long count(Example<S> example) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public <S extends Produit> boolean exists(Example<S> example) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public <S extends Produit, R> R findBy(Example<S> example,
					Function<FetchableFluentQuery<S>, R> queryFunction) {
				// TODO Auto-generated method stub
				return null;
			}
        };

        ConsultationService service = new ConsultationService(new ConsultationRepository() {

			@Override
			public void flush() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public <S extends ConsultationClient> S saveAndFlush(S entity) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends ConsultationClient> List<S> saveAllAndFlush(Iterable<S> entities) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void deleteAllInBatch(Iterable<ConsultationClient> entities) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAllByIdInBatch(Iterable<Long> ids) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAllInBatch() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public ConsultationClient getOne(Long id) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ConsultationClient getById(Long id) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ConsultationClient getReferenceById(Long id) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends ConsultationClient> List<S> findAll(Example<S> example) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends ConsultationClient> List<S> findAll(Example<S> example, Sort sort) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends ConsultationClient> List<S> saveAll(Iterable<S> entities) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<ConsultationClient> findAll() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<ConsultationClient> findAllById(Iterable<Long> ids) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends ConsultationClient> S save(S entity) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Optional<ConsultationClient> findById(Long id) {
				// TODO Auto-generated method stub
				return Optional.empty();
			}

			@Override
			public boolean existsById(Long id) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public long count() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void deleteById(Long id) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void delete(ConsultationClient entity) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAllById(Iterable<? extends Long> ids) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAll(Iterable<? extends ConsultationClient> entities) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deleteAll() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public List<ConsultationClient> findAll(Sort sort) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Page<ConsultationClient> findAll(Pageable pageable) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends ConsultationClient> Optional<S> findOne(Example<S> example) {
				// TODO Auto-generated method stub
				return Optional.empty();
			}

			@Override
			public <S extends ConsultationClient> Page<S> findAll(Example<S> example, Pageable pageable) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <S extends ConsultationClient> long count(Example<S> example) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public <S extends ConsultationClient> boolean exists(Example<S> example) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public <S extends ConsultationClient, R> R findBy(Example<S> example,
					Function<FetchableFluentQuery<S>, R> queryFunction) {
				// TODO Auto-generated method stub
				return null;
			}}, produitRepo);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            service.createConsultation(dto);
        });
    }
}