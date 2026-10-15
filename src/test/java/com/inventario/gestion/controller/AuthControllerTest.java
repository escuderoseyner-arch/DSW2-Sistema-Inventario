package com.inventario.gestion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas del servicio REST de login (usa el usuario admin que crea DataInitializer).
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String loginJson(String usuario, String password) {
        return "{\"usuario\":\"" + usuario + "\",\"password\":\"" + password + "\"}";
    }

    private String obtenerToken() throws Exception {
        String respuesta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("admin", "admin123")))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(respuesta).get("token").asText();
    }

    @Test
    @DisplayName("Login correcto devuelve un token")
    void loginCorrecto() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }

    @Test
    @DisplayName("Contrasena incorrecta responde 401")
    void loginPasswordIncorrecta() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("admin", "otraclave")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Campos vacios responden 400")
    void loginCamposVacios() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("", "")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Sin token no se puede acceder a la API")
    void sinToken() throws Exception {
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Con token si se puede acceder a la API")
    void conToken() throws Exception {
        mockMvc.perform(get("/api/categorias")
                        .header("Authorization", "Bearer " + obtenerToken()))
                .andExpect(status().isOk());
    }
}
