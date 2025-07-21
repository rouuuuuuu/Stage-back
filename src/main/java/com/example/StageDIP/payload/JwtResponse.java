package com.example.StageDIP.payload;

public class JwtResponse {
    private String token;
    private final String type = "Bearer";

    public JwtResponse(String accessToken) {
        this.token = accessToken;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
}
