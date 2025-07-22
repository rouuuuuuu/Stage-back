package com.example.StageDIP.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class JwtService {

    private final Key key;

    // Constructor for manual instantiation (outside Spring)
    public JwtService(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Default constructor for Spring (use your @Value or config to initialize this key)
    public JwtService() {
        // For Spring, inject the key properly or initialize elsewhere
        this.key = null;
    }

    public String generateToken(String username, Set<String> roles) {
        if (key == null) {
            throw new IllegalStateException("Key not initialized! Pass a secret when constructing JwtService.");
        }
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours expiry
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token, String secret) {
        Key keyForParse = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(keyForParse)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsername(String token, String secret) {
        return extractAllClaims(token, secret).getSubject();
    }

    public List<String> getRoles(String token, String secret) {
        return (List<String>) extractAllClaims(token, secret).get("roles");
    }
}
