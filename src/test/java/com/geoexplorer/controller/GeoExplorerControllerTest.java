package com.geoexplorer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração dos endpoints REST expostos pelo
 * {@link GeoExplorerController} via MockMvc.
 */
@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
@AutoConfigureMockMvc
@ActiveProfiles("cli")
class GeoExplorerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ── /trail ──────────────────────────────────────────────────────────────

    @Test
    void getTrail_deveRetornar200_comTecnologiaEModulos() throws Exception {
        mockMvc.perform(get("/trail").param("technology", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technology").value("java"))
                .andExpect(jsonPath("$.level").value("BEGINNER"))
                .andExpect(jsonPath("$.modules").isArray())
                .andExpect(jsonPath("$.modules").isNotEmpty());
    }

    @Test
    void getTrail_deveRetornar404_quandoTecnologiaNaoExiste() throws Exception {
        mockMvc.perform(get("/trail").param("technology", "cobol"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTrail_deveRetornar400_quandoTecnologiaEmBranco() throws Exception {
        mockMvc.perform(get("/trail").param("technology", "   "))
                .andExpect(status().isBadRequest());
    }

    // ── /challenge ──────────────────────────────────────────────────────────

    @Test
    void getChallenge_deveRetornar200_comDesafioParaNivel() throws Exception {
        mockMvc.perform(get("/challenge")
                        .param("technology", "java")
                        .param("level", "BEGINNER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").isNotEmpty())
                .andExpect(jsonPath("$.description").isNotEmpty())
                .andExpect(jsonPath("$.level").value("BEGINNER"));
    }

    @Test
    void getChallenge_deveRetornar404_quandoTecnologiaNaoExiste() throws Exception {
        mockMvc.perform(get("/challenge")
                        .param("technology", "ruby")
                        .param("level", "BEGINNER"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getChallenge_deveRetornar400_quandoNivelInvalido() throws Exception {
        mockMvc.perform(get("/challenge")
                        .param("technology", "java")
                        .param("level", "EXPERT"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getChallenge_deveRetornar400_quandoNivelEmBranco() throws Exception {
        mockMvc.perform(get("/challenge")
                        .param("technology", "java")
                        .param("level", "   "))
                .andExpect(status().isBadRequest());
    }

    // ── /certificate ────────────────────────────────────────────────────────

    @Test
    void getCertificate_deveRetornar200_comCertificado() throws Exception {
        mockMvc.perform(get("/certificate")
                        .param("technology", "java")
                        .param("user", "Ana Lima"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("GEO-EXPLORER — CERTIFICADO")))
                .andExpect(content().string(containsString("Ana Lima")));
    }

    @Test
    void getCertificate_deveRetornar404_quandoTecnologiaNaoExiste() throws Exception {
        mockMvc.perform(get("/certificate")
                        .param("technology", "golang")
                        .param("user", "Ana"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCertificate_deveRetornar400_quandoUsuarioEmBranco() throws Exception {
        mockMvc.perform(get("/certificate")
                        .param("technology", "java")
                        .param("user", "   "))
                .andExpect(status().isBadRequest());
    }
}
