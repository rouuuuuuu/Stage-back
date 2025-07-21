package com.example.StageDIP.controller;

import com.example.StageDIP.payload.LoginRequest;
import com.example.StageDIP.payload.JwtResponse;
import com.example.StageDIP.security.CustomUserDetails;
import com.example.StageDIP.security.JwtUtils;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Cast principal safely
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String jwt = jwtUtils.generateJwtToken(userDetails);

            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (BadCredentialsException ex) {
            // Return 401 unauthorized if login fails
            return ResponseEntity.status(401).body("Invalid username or password");
        } catch (Exception ex) {
            // General fallback
            return ResponseEntity.status(500).body("An error occurred during authentication");
        }
    }
}
