package com.practico.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practico.backend.dto.ProductRequest;
import com.practico.backend.dto.ProductResponse;
import com.practico.backend.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController Integration Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create product via POST /api/products")
    void shouldCreateProductViaPost() throws Exception {
        // Given
        ProductRequest request = new ProductRequest();
        request.setName("Computadora Dell");
        request.setDescription("Laptop Dell Inspiron 15");
        request.setPrice(new BigDecimal("99.99"));
        request.setStock(100);
        request.setCategory("Electronics");

        ProductResponse response = new ProductResponse();
        response.setId("product-id-123");
        response.setName("Computadora Dell");
        response.setDescription("Laptop Dell Inspiron 15");
        response.setPrice(new BigDecimal("99.99"));
        response.setStock(100);
        response.setCategory("Electronics");

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("product-id-123"))
                .andExpect(jsonPath("$.name").value("Computadora Dell"))
                .andExpect(jsonPath("$.description").value("Laptop Dell Inspiron 15"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.stock").value(100))
                .andExpect(jsonPath("$.category").value("Electronics"));
    }

    @Test
    @DisplayName("Should return 400 when product request is invalid")
    void shouldReturn400WhenProductRequestIsInvalid() throws Exception {
        // Given
        ProductRequest invalidRequest = new ProductRequest();
        // Missing required fields: name, price, stock

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when price is negative")
    void shouldReturn400WhenPriceIsNegative() throws Exception {
        // Given
        ProductRequest request = new ProductRequest();
        request.setName("Computadora Dell");
        request.setPrice(new BigDecimal("-10.00"));
        request.setStock(100);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when stock is negative")
    void shouldReturn400WhenStockIsNegative() throws Exception {
        // Given
        ProductRequest request = new ProductRequest();
        request.setName("Computadora Dell");
        request.setPrice(new BigDecimal("99.99"));
        request.setStock(-10);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

