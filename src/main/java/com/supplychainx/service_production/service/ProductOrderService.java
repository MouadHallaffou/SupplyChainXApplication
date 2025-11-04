package com.supplychainx.service_production.service;

import com.supplychainx.service_production.dto.ProductOrderRequestDTO;
import com.supplychainx.service_production.dto.ProductOrderResponseDTO;
import org.springframework.data.domain.Page;

public interface ProductOrderService {
    ProductOrderResponseDTO createProductOrder(ProductOrderRequestDTO productOrderRequestDTO);
    ProductOrderResponseDTO getProductOrderById(Long productOrderId);
    ProductOrderResponseDTO updateProductOrder(Long productOrderId, ProductOrderRequestDTO productOrderRequestDTO);
    void deleteProductOrder(Long productOrderId);
    Page<ProductOrderResponseDTO> getAllProductOrders(int page, int size);
    ProductOrderResponseDTO startProduction(Long productOrderId);
    ProductOrderResponseDTO completeProduction(Long productOrderId);
    ProductOrderResponseDTO cancelProduction(Long productOrderId);
}
