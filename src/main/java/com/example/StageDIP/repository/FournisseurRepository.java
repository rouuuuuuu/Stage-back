package com.example.StageDIP.repository;

import com.example.StageDIP.model.Fournisseur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long>, JpaSpecificationExecutor<Fournisseur> {

    @EntityGraph(attributePaths = {"produits", "factures"})
    Page<Fournisseur> findAll(Specification<Fournisseur> spec, Pageable pageable);

}
