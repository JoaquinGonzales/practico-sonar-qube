package com.practico.backend.service;

import com.practico.backend.dto.CustomerRequest;
import com.practico.backend.dto.CustomerResponse;
import com.practico.backend.entity.Customer;
import com.practico.backend.repository.CustomerRepository;
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
@DisplayName("CustomerService Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerRequest customerRequest;
    private Customer savedCustomer;

    @BeforeEach
    void setUp() {
        customerRequest = new CustomerRequest();
        customerRequest.setFirstName("Juan Jose");
        customerRequest.setLastName("Miranda");
        customerRequest.setEmail("juanjose.miranda@example.com");
        customerRequest.setPhone("70112233");
        customerRequest.setAddress("Av. 20 Octubre, Torre Azul");

        savedCustomer = new Customer();
        savedCustomer.setId("customer-id-123");
        savedCustomer.setFirstName("Juan Jose");
        savedCustomer.setLastName("Miranda");
        savedCustomer.setEmail("juanjose.miranda@example.com");
        savedCustomer.setPhone("70112233");
        savedCustomer.setAddress("Av. 20 Octubre, Torre Azul");
    }

    @Test
    @DisplayName("Should create customer successfully")
    void shouldCreateCustomerSuccessfully() {
        // Given
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        // When
        CustomerResponse response = customerService.createCustomer(customerRequest);

        // Then
        assertNotNull(response);
        assertEquals("customer-id-123", response.getId());
        assertEquals("Juan Jose", response.getFirstName());
        assertEquals("Miranda", response.getLastName());
        assertEquals("juanjose.miranda@example.com", response.getEmail());
        assertEquals("70112233", response.getPhone());
        assertEquals("Av. 20 Octubre, Torre Azul", response.getAddress());

        verify(customerRepository, times(1)).existsByEmail(customerRequest.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw exception when customer email already exists")
    void shouldThrowExceptionWhenCustomerEmailAlreadyExists() {
        // Given
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerService.createCustomer(customerRequest);
        });

        assertEquals("Customer with email juanjose.miranda@example.com already exists", exception.getMessage());
        verify(customerRepository, times(1)).existsByEmail(customerRequest.getEmail());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should map customer request to entity correctly")
    void shouldMapCustomerRequestToEntityCorrectly() {
        // Given
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            customer.setId("customer-id-123");
            return customer;
        });

        // When
        CustomerResponse response = customerService.createCustomer(customerRequest);

        // Then
        assertNotNull(response);
        assertEquals(customerRequest.getFirstName(), response.getFirstName());
        assertEquals(customerRequest.getLastName(), response.getLastName());
        assertEquals(customerRequest.getEmail(), response.getEmail());
        assertEquals(customerRequest.getPhone(), response.getPhone());
        assertEquals(customerRequest.getAddress(), response.getAddress());
    }

    @Test
    @DisplayName("Should handle customer creation with null optional fields")
    void shouldHandleCustomerCreationWithNullOptionalFields() {
        // Given
        customerRequest.setPhone(null);
        customerRequest.setAddress(null);
        
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            customer.setId("customer-id-123");
            return customer;
        });

        // When
        CustomerResponse response = customerService.createCustomer(customerRequest);

        // Then
        assertNotNull(response);
        assertNull(response.getPhone());
        assertNull(response.getAddress());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
}

