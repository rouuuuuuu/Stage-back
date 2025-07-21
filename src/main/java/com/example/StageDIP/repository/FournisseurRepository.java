package com.example.StageDIP.repository;

import com.example.StageDIP.model.Fournisseur;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long>, JpaSpecificationExecutor<Fournisseur> {
	@EntityGraph(attributePaths = {"produits", "factures"})
	@Query("SELECT f FROM Fournisseur f")
	List<Fournisseur> findAllWithDetails();}
    // No need for your old @Query anymore, specs will do the work

