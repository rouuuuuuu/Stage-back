package com.example.StageDIP.repository;

import com.example.StageDIP.model.Facture;
import com.example.StageDIP.model.Fournisseur;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FactureRepository extends JpaRepository<Facture, Long> {
    List<Facture> findByProduitsIdIn(List<Long> produitIds);

	List<Facture> findByFournisseur(Fournisseur f);
}
