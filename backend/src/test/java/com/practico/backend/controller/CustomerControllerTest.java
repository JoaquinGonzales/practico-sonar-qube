package com.practico.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practico.backend.dto.CustomerRequest;
import com.practico.backend.dto.CustomerResponse;
import com.practico.backend.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@DisplayName("CustomerController Integration Tests")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create customer via POST /api/customers")
    void shouldCreateCustomerViaPost() throws Exception {
        // Given
        CustomerRequest request = new CustomerRequest();
        request.setFirstName("Juan Jose");
        request.setLastName("Miranda");
        request.setEmail("juanjose.miranda@example.com");
        request.setPhone("70112233");
        request.setAddress("Av. 20 Octubre, Torre Azul");

        CustomerResponse response = new CustomerResponse();
        response.setId("customer-id-123");
        response.setFirstName("Juan Jose");
        response.setLastName("Miranda");
        response.setEmail("juanjose.miranda@example.com");
        response.setPhone("70112233");
        response.setAddress("Av. 20 Octubre, Torre Azul");

        when(customerService.createCustomer(any(CustomerRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("customer-id-123"))
                .andExpect(jsonPath("$.firstName").value("Juan Jose"))
                .andExpect(jsonPath("$.lastName").value("Miranda"))
                .andExpect(jsonPath("$.email").value("juanjose.miranda@example.com"))
                .andExpect(jsonPath("$.phone").value("70112233"))
                .andExpect(jsonPath("$.address").value("Av. 20 Octubre, Torre Azul"));
    }

    @Test
    @DisplayName("Should return 400 when customer request is invalid")
    void shouldReturn400WhenCustomerRequestIsInvalid() throws Exception {
        // Given
        CustomerRequest invalidRequest = new CustomerRequest();
        // Missing required fields: firstName, lastName, email

        // When & Then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when email is invalid")
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        // Given
        CustomerRequest request = new CustomerRequest();
        request.setFirstName("Juan Jose");
        request.setLastName("Miranda");
        request.setEmail("invalid-email"); // Invalid email format

        // When & Then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when required fields are missing")
    void shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {
        // Given
        CustomerRequest request = new CustomerRequest();
        request.setEmail("juanjose.miranda@example.com");
        // Missing firstName and lastName

        // When & Then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

