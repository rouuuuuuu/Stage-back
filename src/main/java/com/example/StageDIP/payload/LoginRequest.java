package com.example.StageDIP.payload;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String u) { username = u; }

    public String getPassword() { return password; }
    public void setPassword(String p) { password = p; }
}
