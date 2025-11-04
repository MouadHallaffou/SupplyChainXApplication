package com.supplychainx.service_production.service;

import com.supplychainx.service_production.dto.ProductRequestDTO;
import com.supplychainx.service_production.dto.ProductResponseDTO;
import org.springframework.data.domain.Page;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);
    ProductResponseDTO getProductById(Long productId);
    ProductResponseDTO updateProduct(ProductRequestDTO productRequestDTO, Long id);
    void deleteProduct(Long productId);
    Page<ProductResponseDTO> getAllProducts(int page, int size);
    ProductResponseDTO getProductByName(String name);
}
