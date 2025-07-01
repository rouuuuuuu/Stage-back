package com.example.StageDIP.repository;

import com.example.StageDIP.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email); // ✅ Clean and clear
}
