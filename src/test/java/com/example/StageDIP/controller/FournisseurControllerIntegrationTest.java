package com.example.StageDIP.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.hamcrest.Matchers;

@SpringBootTest
@AutoConfigureMockMvc
public class FournisseurControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFilterFournisseurs() throws Exception {
        mockMvc.perform(get("/api/fournisseurs/filter")
                .param("minPrix", "50")
                .param("maxPrix", "300")
                .param("minNotation", "3.5")
                .param("categorie", "Électroménager")
                .param("nomProduit", "frigo")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(Matchers.greaterThanOrEqualTo(0)));
    }
}
