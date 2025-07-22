package com.example.StageDIP.util;

import com.example.StageDIP.service.JwtService;
import java.util.Set;

public class TokenGenerator {
    public static void main(String[] args) {
        String secret = "ThisIsYourSuperSecretKeyThatMustBeAtLeast256BitsLong1234567890abcdef";
        JwtService jwtService = new JwtService(secret);
        
        String token = jwtService.generateToken("rourou", Set.of("USER", "ADMIN"));
        System.out.println("Generated Token: " + token);
    }
}
