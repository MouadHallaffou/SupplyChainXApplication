package com.supplychainx.service.service_production;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_production.dto.Request.ProductRequestDTO;
import com.supplychainx.service_production.dto.Response.ProductResponseDTO;
import com.supplychainx.service_production.mapper.ProductMapper;
import com.supplychainx.service_production.model.Product;
import com.supplychainx.service_production.repository.ProductRepository;
import com.supplychainx.service_production.service.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

        import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProductMapper productMapper;
    @InjectMocks private ProductServiceImpl productService;

    @Test void createProduct_ShouldReturnResponseDTO() {
        ProductRequestDTO request = new ProductRequestDTO();
        Product product = new Product();
        ProductResponseDTO response = new ProductResponseDTO(1L, "Updated", 11, 150.0, 15,   null, null);

        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDto(product)).thenReturn(response);

        ProductResponseDTO result = productService.createProduct(request);

        assertEquals(response, result);
    }

    @Test void getProductById_ShouldReturnResponseDTO_WhenExists() {
        Product product = new Product();
        ProductResponseDTO response = new ProductResponseDTO(1L, "Updated", 11, 150.0, 15,   null, null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDto(product)).thenReturn(response);

        ProductResponseDTO result = productService.getProductById(1L);

        assertEquals(response, result);
    }

    @Test void getProductById_ShouldThrow_WhenNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test void updateProduct_ShouldReturnUpdatedDTO() {
        ProductRequestDTO request = new ProductRequestDTO();
        Product existing = new Product();
        ProductResponseDTO response = new ProductResponseDTO(1L, "Updated", 11, 150.0, 15,   null, null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);
        when(productMapper.toResponseDto(existing)).thenReturn(response);

        ProductResponseDTO result = productService.updateProduct(request, 1L);

        assertEquals(response, result);
    }

    @Test void deleteProduct_ShouldDelete_WhenExists() {
        when(productRepository.existsById(1L)).thenReturn(true);
        productService.deleteProduct(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test void getAllProducts_ShouldReturnPage() {
        Product product = new Product();
        ProductResponseDTO response = new ProductResponseDTO(1L, "Updated", 11, 150.0, 15,   null, null);

        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toResponseDto(product)).thenReturn(response);

        Page<ProductResponseDTO> result = productService.getAllProducts(0, 10);

        assertFalse(result.getContent().isEmpty());
    }

    @Test void getProductByName_ShouldReturnResponseDTO() {
        Product product = new Product();
        ProductResponseDTO response = new ProductResponseDTO(1L, "Updated", 11, 150.0, 15,   null, null);

        when(productRepository.findByNameIgnoreCase("Product1")).thenReturn(Optional.of(product));
        when(productMapper.toResponseDto(product)).thenReturn(response);

        ProductResponseDTO result = productService.getProductByName("Product1");

        assertEquals(response, result);
    }
}