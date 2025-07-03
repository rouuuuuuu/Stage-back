package com.example.StageDIP.service;

import com.example.StageDIP.model.Client;
import com.example.StageDIP.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepo;

    public ClientService(ClientRepository clientRepo) {
        this.clientRepo = clientRepo;
    }

    public Client registerClient(Client client) {
        // You *could* add checks here like if email already exists
        Optional<Client> existing = clientRepo.findByEmail(client.getEmail());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Email already registered, princess.");
        }
        // Here you could hash password before saving, but let’s keep it simple for now
        return clientRepo.save(client);
    }

    public List<Client> getAllClients() {
        return clientRepo.findAll();
    }

    public Optional<Client> getClientById(Long id) {
        return clientRepo.findById(id);
    }

    public Client updateClient(Long id, Client updatedClient) {
        Client client = clientRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Client not found with id: " + id));

        client.setNom(updatedClient.getNom());
        client.setEmail(updatedClient.getEmail());
        client.setPassword(updatedClient.getPassword()); // Ideally hashed
        client.setRole(updatedClient.getRole());

        return clientRepo.save(client);
    }

    public void deleteClient(Long id) {
        if (!clientRepo.existsById(id)) {
            throw new IllegalArgumentException("Client not found with id: " + id);
        }
        clientRepo.deleteById(id);
    }
}
