package com.inventario.gestion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventario.gestion.model.Categoria;
import com.inventario.gestion.model.Producto;
import com.inventario.gestion.repository.CategoriaRepository;
import com.inventario.gestion.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MovimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private String token;
    private Producto producto;

    @BeforeEach
    void prepararDatos() throws Exception {
        String respuesta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuario\":\"admin\",\"password\":\"admin123\"}"))
                .andReturn().getResponse().getContentAsString();
        token = "Bearer " + objectMapper.readTree(respuesta).get("token").asText();

        Categoria categoria = categoriaRepository.save(
                Categoria.builder().nombre("Cat-" + UUID.randomUUID().toString().substring(0, 8)).build());
        producto = productoRepository.save(Producto.builder()
                .nombre("Producto-" + UUID.randomUUID().toString().substring(0, 8))
                .precio(new BigDecimal("10.00"))
                .stock(10)
                .stockMinimo(3)
                .categoria(categoria)
                .build());
    }

    private String json(Long idProducto, Object cantidad) {
        return "{\"idProducto\":" + idProducto + ",\"cantidad\":" + cantidad + "}";
    }

    private Integer stockActual() {
        return productoRepository.findById(producto.getId()).orElseThrow().getStock();
    }

    @Test
    @DisplayName("Entrada responde 201 y aumenta el stock")
    void entrada() throws Exception {
        mockMvc.perform(post("/api/movimientos/entrada")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(producto.getId(), 5)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("ENTRADA"))
                .andExpect(jsonPath("$.cantidad").value(5))
                .andExpect(jsonPath("$.usuario").value("admin"));

        assertThat(stockActual()).isEqualTo(15);
    }

    @Test
    @DisplayName("Salida responde 201 y disminuye el stock")
    void salida() throws Exception {
        mockMvc.perform(post("/api/movimientos/salida")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(producto.getId(), 4)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("SALIDA"));

        assertThat(stockActual()).isEqualTo(6);
    }

    @Test
    @DisplayName("Salida mayor al stock responde 409 y no cambia el stock")
    void salidaMayorAlStock() throws Exception {
        mockMvc.perform(post("/api/movimientos/salida")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(producto.getId(), 11)))
                .andExpect(status().isConflict());

        assertThat(stockActual()).isEqualTo(10);
    }

    @Test
    @DisplayName("Cantidad cero o negativa responde 400")
    void cantidadInvalida() throws Exception {
        mockMvc.perform(post("/api/movimientos/entrada")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(producto.getId(), 0)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/movimientos/salida")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(producto.getId(), -3)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Producto inexistente responde 404")
    void productoInexistente() throws Exception {
        mockMvc.perform(post("/api/movimientos/entrada")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(999999L, 1)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Sin token responde 401")
    void sinToken() throws Exception {
        mockMvc.perform(post("/api/movimientos/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(producto.getId(), 1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Los movimientos del producto quedan en el historial")
    void historialPorProducto() throws Exception {
        mockMvc.perform(post("/api/movimientos/entrada")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(producto.getId(), 2)));
        mockMvc.perform(post("/api/movimientos/salida")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(producto.getId(), 1)));

        mockMvc.perform(get("/api/movimientos/producto/" + producto.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Consultar un movimiento inexistente responde 404")
    void movimientoInexistente() throws Exception {
        mockMvc.perform(get("/api/movimientos/999999").header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Inventario por producto y stock bajo")
    void inventario() throws Exception {
        mockMvc.perform(get("/api/inventario/producto/" + producto.getId()).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(10))
                .andExpect(jsonPath("$.stockBajo").value(false));

        mockMvc.perform(post("/api/movimientos/salida")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(producto.getId(), 7)));

        mockMvc.perform(get("/api/inventario/stock-bajo").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].idProducto").value(hasItem(producto.getId().intValue())));

        mockMvc.perform(get("/api/inventario").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].idProducto").value(hasItem(producto.getId().intValue())));

        mockMvc.perform(get("/api/inventario/producto/999999").header("Authorization", token))
                .andExpect(status().isNotFound());
    }
}
