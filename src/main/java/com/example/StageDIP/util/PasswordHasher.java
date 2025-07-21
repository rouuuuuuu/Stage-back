package com.example.StageDIP.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "mypassword123"; 
        System.out.println(encoder.encode(rawPassword));
    }
}
