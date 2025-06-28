package com.example.StageDIP.repository;

import com.example.StageDIP.model.Facture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FactureRepository extends JpaRepository<Facture, Long> {}
