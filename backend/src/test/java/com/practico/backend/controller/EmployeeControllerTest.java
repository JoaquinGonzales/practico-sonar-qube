package com.practico.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practico.backend.dto.EmployeeRequest;
import com.practico.backend.dto.EmployeeResponse;
import com.practico.backend.service.EmployeeService;
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

@WebMvcTest(EmployeeController.class)
@DisplayName("EmployeeController Integration Tests")
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create employee via POST /api/employees")
    void shouldCreateEmployeeViaPost() throws Exception {
        // Given
        EmployeeRequest request = new EmployeeRequest();
        request.setFirstName("Cesar");
        request.setLastName("Vincenti");
        request.setEmail("cesar.vincenti@example.com");
        request.setPhone("70112233");
        request.setPosition("Software Engineer");

        EmployeeResponse response = new EmployeeResponse();
        response.setId("employee-id-123");
        response.setFirstName("Cesar");
        response.setLastName("Vincenti");
        response.setEmail("cesar.vincenti@example.com");
        response.setPhone("70112233");
        response.setPosition("Software Engineer");

        when(employeeService.createEmployee(any(EmployeeRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("employee-id-123"))
                .andExpect(jsonPath("$.firstName").value("Cesar"))
                .andExpect(jsonPath("$.lastName").value("Vincenti"))
                .andExpect(jsonPath("$.email").value("cesar.vincenti@example.com"))
                .andExpect(jsonPath("$.phone").value("70112233"))
                .andExpect(jsonPath("$.position").value("Software Engineer"));
    }

    @Test
    @DisplayName("Should return 400 when employee request is invalid")
    void shouldReturn400WhenEmployeeRequestIsInvalid() throws Exception {
        // Given
        EmployeeRequest invalidRequest = new EmployeeRequest();
        // Missing required fields: firstName, lastName, email, position

        // When & Then
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when email is invalid")
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        // Given
        EmployeeRequest request = new EmployeeRequest();
        request.setFirstName("Cesar");
        request.setLastName("Vincenti");
        request.setEmail("invalid-email"); // Invalid email format
        request.setPosition("Software Engineer");

        // When & Then
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when required fields are missing")
    void shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {
        // Given
        EmployeeRequest request = new EmployeeRequest();
        request.setEmail("cesar.vincenti@example.com");
        // Missing firstName, lastName, and position

        // When & Then
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

