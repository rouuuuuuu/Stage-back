// src/main/java/com/example/gestionfournisseurs/repository/ConsultationClientRepository.java
package com.example.StageDIP.repository;

import com.example.StageDIP.model.ConsultationClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRepository extends JpaRepository<ConsultationClient, Long> {
}