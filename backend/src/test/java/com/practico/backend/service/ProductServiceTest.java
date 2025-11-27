package com.practico.backend.service;

import com.practico.backend.dto.ProductRequest;
import com.practico.backend.dto.ProductResponse;
import com.practico.backend.entity.Product;
import com.practico.backend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductRequest productRequest;
    private Product savedProduct;

    @BeforeEach
    void setUp() {
        productRequest = new ProductRequest();
        productRequest.setName("Computadora Dell");
        productRequest.setDescription("Laptop Dell Inspiron 15");
        productRequest.setPrice(new BigDecimal("99.99"));
        productRequest.setStock(100);
        productRequest.setCategory("Electronics");

        savedProduct = new Product();
        savedProduct.setId("product-id-123");
        savedProduct.setName("Computadora Dell");
        savedProduct.setDescription("Laptop Dell Inspiron 15");
        savedProduct.setPrice(new BigDecimal("99.99"));
        savedProduct.setStock(100);
        savedProduct.setCategory("Electronics");
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        ProductResponse response = productService.createProduct(productRequest);

        // Then
        assertNotNull(response);
        assertEquals("product-id-123", response.getId());
        assertEquals("Computadora Dell", response.getName());
        assertEquals("Laptop Dell Inspiron 15", response.getDescription());
        assertEquals(new BigDecimal("99.99"), response.getPrice());
        assertEquals(100, response.getStock());
        assertEquals("Electronics", response.getCategory());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should map product request to entity correctly")
    void shouldMapProductRequestToEntityCorrectly() {
        // Given
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId("product-id-123");
            return product;
        });

        // When
        ProductResponse response = productService.createProduct(productRequest);

        // Then
        assertNotNull(response);
        assertEquals(productRequest.getName(), response.getName());
        assertEquals(productRequest.getDescription(), response.getDescription());
        assertEquals(productRequest.getPrice(), response.getPrice());
        assertEquals(productRequest.getStock(), response.getStock());
        assertEquals(productRequest.getCategory(), response.getCategory());
    }

    @Test
    @DisplayName("Should handle product creation with null optional fields")
    void shouldHandleProductCreationWithNullOptionalFields() {
        // Given
        productRequest.setDescription(null);
        productRequest.setCategory(null);
        
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId("product-id-123");
            return product;
        });

        // When
        ProductResponse response = productService.createProduct(productRequest);

        // Then
        assertNotNull(response);
        assertNull(response.getDescription());
        assertNull(response.getCategory());
        verify(productRepository, times(1)).save(any(Product.class));
    }
}

