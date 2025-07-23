package com.example.StageDIP.repository;

import com.example.StageDIP.model.ConsultationClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationClientRepository extends JpaRepository<ConsultationClient, Long> {

    Page<ConsultationClient> findAll(Pageable pageable);

    Page<ConsultationClient> findByClientId(Long clientId, Pageable pageable);
}
