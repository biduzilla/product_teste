package com.example.ms_product.controllers;

import com.example.ms_product.dtos.ProductRequest;
import com.example.ms_product.enums.ProductStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ProductControllerIT {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void deveCriarEBuscarProduto() throws Exception {
        var req = new ProductRequest(
                "Notebook",
                "Dell Inspiron",
                new BigDecimal("3500.00"),
                "Eletrônicos",
                ProductStatus.ACTIVE
        );

        String body = mvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Notebook"))
                .andExpect(jsonPath("$.description").value("Dell Inspiron"))
                .andExpect(jsonPath("$.price").value(3500.00))
                .andExpect(jsonPath("$.category").value("Eletrônicos"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn().getResponse().getContentAsString();

        String id = json.readTree(body).get("id").asText();

        mvc.perform(get("/api/produtos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Notebook"));
    }

    @Test
    void deveRetornar422QuandoNomeInvalido() throws Exception {
        var req = new ProductRequest(
                "",
                null,
                new BigDecimal("100.00"),
                "Categoria válida",
                ProductStatus.ACTIVE
        );

        mvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.fields.name").exists());
    }

    @Test
    void deveRetornar422QuandoCamposObrigatoriosFaltando() throws Exception {
        var req = new ProductRequest(null, null, null, null, null);

        mvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.fields.name").exists())
                .andExpect(jsonPath("$.fields.price").exists())
                .andExpect(jsonPath("$.fields.category").exists())
                .andExpect(jsonPath("$.fields.status").exists());
    }

    @Test
    void deveRetornar404QuandoBuscaProdutoInexistente() throws Exception {
        mvc.perform(get("/api/produtos/{id}", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }
}