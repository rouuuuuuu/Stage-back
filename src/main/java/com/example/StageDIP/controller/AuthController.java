package com.example.StageDIP.controller;

import com.example.StageDIP.model.Client;
import com.example.StageDIP.repository.ClientRepository;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ClientRepository clientRepo;

    public AuthController(ClientRepository clientRepo) {
        this.clientRepo = clientRepo;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Client client = clientRepo.findByEmail(loginRequest.getEmail()).orElse(null);

        if (client == null || !client.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(401).body("Email ou mot de passe incorrect");
        }

        return ResponseEntity.ok(client); // client contains role
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Client input) {
        Optional<Client> clientOpt = clientRepo.findByEmail(input.getEmail());
        if (clientOpt.isPresent()) {
            return ResponseEntity.status(409).body("Email déjà utilisé");
        }

        Client saved = clientRepo.save(new Client(
            input.getNom(),
            input.getEmail(),
            input.getPassword(),
            input.getRole() // "CLIENT" or "ADMIN"
        ));

        return ResponseEntity.status(201).body(saved);
    }


    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }


   
}
