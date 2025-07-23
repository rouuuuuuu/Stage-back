package com.example.StageDIP.service;

import com.example.StageDIP.model.Client;
import com.example.StageDIP.model.ConsultationClient;
import com.example.StageDIP.repository.ClientRepository;
import com.example.StageDIP.repository.ConsultationClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConsultationService {

    @Autowired
    private ConsultationClientRepository consultationClientRepository;

    @Autowired
    private ClientRepository clientRepository;

    public ConsultationClient addConsultation(Long clientId, ConsultationClient consultationClient) {
        Optional<Client> optionalClient = clientRepository.findById(clientId);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            consultationClient.setClient(client);
            return consultationClientRepository.save(consultationClient);
        } else {
            throw new IllegalArgumentException("Client not found with ID: " + clientId);
        }
    }

    public Page<ConsultationClient> getConsultationsByClientId(Long clientId, Pageable pageable) {
        return consultationClientRepository.findByClientId(clientId, pageable);
    }

    public Page<ConsultationClient> getAllConsultations(Pageable pageable) {
        return consultationClientRepository.findAll(pageable);
    }
}
