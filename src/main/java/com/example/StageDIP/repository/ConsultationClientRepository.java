package com.example.StageDIP.repository;

import com.example.StageDIP.model.Client;
import com.example.StageDIP.model.ConsultationClient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConsultationClientRepository extends JpaRepository<ConsultationClient, Long> {
    List<ConsultationClient> findByClientId(Long clientId);

}
