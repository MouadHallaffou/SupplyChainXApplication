package com.supplychainx.service_production.controller;

import com.supplychainx.handler.GlobalSuccessHandler;
import com.supplychainx.service_production.dto.Response.ProductOrderResponseDTO;
import com.supplychainx.service_production.dto.Request.ProductOrderRequestDTO;
import com.supplychainx.service_production.service.ProductOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products-orders")
public class ProductOrderController {

    private final ProductOrderService productOrderService;

//    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PRODUCTION', 'PLANIFICATEUR', 'SUPERVISEUR_PRODUCTION')")
    @PostMapping
    public ProductOrderResponseDTO createProductOrder(@Valid @RequestBody ProductOrderRequestDTO productOrderRequestDTO) {
        return productOrderService.createProductOrder(productOrderRequestDTO);
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PRODUCTION', 'PLANIFICATEUR', 'SUPERVISEUR_PRODUCTION')")
    @GetMapping("/{id}")
    public ProductOrderResponseDTO getProductOrderById(@PathVariable("id") Long id) {
        return productOrderService.getProductOrderById(id);
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PRODUCTION', 'PLANIFICATEUR', 'SUPERVISEUR_PRODUCTION')")
    @PutMapping("/{id}")
    public ProductOrderResponseDTO updateProductOrder(@Valid @PathVariable("id") Long id, @RequestBody ProductOrderRequestDTO productOrderRequestDTO) {
        return productOrderService.updateProductOrder(id, productOrderRequestDTO);
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PRODUCTION', 'PLANIFICATEUR', 'SUPERVISEUR_PRODUCTION')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProductOrder(@PathVariable("id") Long id) {
        productOrderService.deleteProductOrder(id);
        return GlobalSuccessHandler.handleDeleted("Product order deleted successfully");
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PRODUCTION', 'PLANIFICATEUR', 'SUPERVISEUR_PRODUCTION')")
    @GetMapping
    public Page<ProductOrderResponseDTO> getAllProductOrders(int page, int size) {
        return productOrderService.getAllProductOrders(page, size);
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PRODUCTION', 'PLANIFICATEUR', 'SUPERVISEUR_PRODUCTION')")
    @PutMapping("/{id}/start")
    public ResponseEntity<Map<String, Object>> startProduction(@PathVariable("id") Long id) {
        ProductOrderResponseDTO responseDTO = productOrderService.startProduction(id);
        return GlobalSuccessHandler.handleSuccessWithData("Production started successfully", responseDTO);
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PRODUCTION', 'PLANIFICATEUR', 'SUPERVISEUR_PRODUCTION')")
    @PutMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> completeProduction(@PathVariable("id") Long id) {
        ProductOrderResponseDTO responseDTO = productOrderService.completeProduction(id);
        return GlobalSuccessHandler.handleSuccessWithData("Production completed successfully", responseDTO);
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PRODUCTION', 'PLANIFICATEUR', 'SUPERVISEUR_PRODUCTION')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelProduction(@PathVariable("id") Long id) {
        ProductOrderResponseDTO responseDTO = productOrderService.cancelProduction(id);
        return GlobalSuccessHandler.handleSuccessWithData("Production cancelled successfully", responseDTO);
    }

}