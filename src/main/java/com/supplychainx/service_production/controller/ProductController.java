package com.supplychainx.service_production.controller;

import com.supplychainx.handler.GlobalSuccessHandler;
import com.supplychainx.service_production.dto.ProductRequestDTO;
import com.supplychainx.service_production.dto.ProductResponseDTO;
import com.supplychainx.service_production.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ProductResponseDTO createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        return productService.createProduct(productRequestDTO);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return GlobalSuccessHandler.handleDeleted("Product deleted successfully");
    }

    @GetMapping
    public Page<ProductResponseDTO> getAllProducts(int page, int size) {
        return productService.getAllProducts(page, size);
    }

    @GetMapping("/search")
    public ProductResponseDTO getProductByName(@RequestParam("name") String name) {
        return productService.getProductByName(name);
    }

}
