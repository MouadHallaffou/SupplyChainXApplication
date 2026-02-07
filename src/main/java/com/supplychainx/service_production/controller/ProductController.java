package com.supplychainx.service_production.controller;

import com.supplychainx.handler.GlobalSuccessHandler;
import com.supplychainx.service_production.dto.Request.ProductRequestDTO;
import com.supplychainx.service_production.dto.Response.ProductResponseDTO;
import com.supplychainx.service_production.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PRODUCTION', 'PLANIFICATEUR', 'SUPERVISEUR_PRODUCTION')")
    @PostMapping
    public ProductResponseDTO createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        return productService.createProduct(productRequestDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PRODUCTION', 'PLANIFICATEUR', 'SUPERVISEUR_PRODUCTION')")
    @GetMapping("/{id}")
    public ProductResponseDTO getProductById(@PathVariable("id") Long id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    public ProductResponseDTO updateProduct(@PathVariable("id") Long id,
                                            @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        System.out.println("Updating product with ID: " + id);
        return productService.updateProduct(productRequestDTO, id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PRODUCTION', 'PLANIFICATEUR', 'SUPERVISEUR_PRODUCTION')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return GlobalSuccessHandler.handleDeleted("Product deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PRODUCTION', 'PLANIFICATEUR', 'SUPERVISEUR_PRODUCTION')")
    @GetMapping
    public Page<ProductResponseDTO> getAllProducts(int page, int size) {
        return productService.getAllProducts(page, size);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_PRODUCTION', 'PLANIFICATEUR', 'SUPERVISEUR_PRODUCTION')")
    @GetMapping("/search")
    public ProductResponseDTO getProductByName(@RequestParam("name") String name) {
        return productService.getProductByName(name);
    }

}