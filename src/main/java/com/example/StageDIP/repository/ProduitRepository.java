package com.example.StageDIP.repository;

import com.example.StageDIP.model.Fournisseur;
import com.example.StageDIP.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit, Long>, JpaSpecificationExecutor<Produit> {

    @Query("SELECT p FROM Produit p WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Produit> searchByNomPartiel(@Param("query") String query);

    List<Produit> findByFournisseurIn(List<Fournisseur> fournisseurs);

}
