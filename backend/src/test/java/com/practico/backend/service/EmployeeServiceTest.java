package com.practico.backend.service;

import com.practico.backend.dto.EmployeeRequest;
import com.practico.backend.dto.EmployeeResponse;
import com.practico.backend.entity.Employee;
import com.practico.backend.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService Tests")
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private EmployeeRequest employeeRequest;
    private Employee savedEmployee;

    @BeforeEach
    void setUp() {
        employeeRequest = new EmployeeRequest();
        employeeRequest.setFirstName("Cesar");
        employeeRequest.setLastName("Vincenti");
        employeeRequest.setEmail("cesar.vincenti@example.com");
        employeeRequest.setPhone("70112233");
        employeeRequest.setPosition("Software Engineer");

        savedEmployee = new Employee();
        savedEmployee.setId("employee-id-123");
        savedEmployee.setFirstName("Cesar");
        savedEmployee.setLastName("Vincenti");
        savedEmployee.setEmail("cesar.vincenti@example.com");
        savedEmployee.setPhone("70112233");
        savedEmployee.setPosition("Software Engineer");
    }

    @Test
    @DisplayName("Should create employee successfully")
    void shouldCreateEmployeeSuccessfully() {
        // Given
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        // When
        EmployeeResponse response = employeeService.createEmployee(employeeRequest);

        // Then
        assertNotNull(response);
        assertEquals("employee-id-123", response.getId());
        assertEquals("Cesar", response.getFirstName());
        assertEquals("Vincenti", response.getLastName());
        assertEquals("cesar.vincenti@example.com", response.getEmail());
        assertEquals("70112233", response.getPhone());
        assertEquals("Software Engineer", response.getPosition());

        verify(employeeRepository, times(1)).existsByEmail(employeeRequest.getEmail());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw exception when employee email already exists")
    void shouldThrowExceptionWhenEmployeeEmailAlreadyExists() {
        // Given
        when(employeeRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            employeeService.createEmployee(employeeRequest);
        });

        assertEquals("Employee with email cesar.vincenti@example.com already exists", exception.getMessage());
        verify(employeeRepository, times(1)).existsByEmail(employeeRequest.getEmail());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should map employee request to entity correctly")
    void shouldMapEmployeeRequestToEntityCorrectly() {
        // Given
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee employee = invocation.getArgument(0);
            employee.setId("employee-id-123");
            return employee;
        });

        // When
        EmployeeResponse response = employeeService.createEmployee(employeeRequest);

        // Then
        assertNotNull(response);
        assertEquals(employeeRequest.getFirstName(), response.getFirstName());
        assertEquals(employeeRequest.getLastName(), response.getLastName());
        assertEquals(employeeRequest.getEmail(), response.getEmail());
        assertEquals(employeeRequest.getPhone(), response.getPhone());
        assertEquals(employeeRequest.getPosition(), response.getPosition());
    }

    @Test
    @DisplayName("Should handle employee creation with null optional fields")
    void shouldHandleEmployeeCreationWithNullOptionalFields() {
        // Given
        employeeRequest.setPhone(null);
        
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee employee = invocation.getArgument(0);
            employee.setId("employee-id-123");
            return employee;
        });

        // When
        EmployeeResponse response = employeeService.createEmployee(employeeRequest);

        // Then
        assertNotNull(response);
        assertNull(response.getPhone());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }
}

