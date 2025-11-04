package com.supplychainx.service_production.service;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_production.dto.ProductRequestDTO;
import com.supplychainx.service_production.dto.ProductResponseDTO;
import com.supplychainx.service_production.mapper.ProductMapper;
import com.supplychainx.service_production.model.Product;
import com.supplychainx.service_production.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = productMapper.toEntity(productRequestDTO);
        Product saveProduct = productRepository.save(product);
        return productMapper.toResponseDto(saveProduct);
    }

    @Override
    public ProductResponseDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        return productMapper.toResponseDto(product);
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(ProductRequestDTO productRequestDTO, Long id) {
        Product existingProduct = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        System.out.println("Existing product before update: " + existingProduct);
        System.out.println("ProductRequestDTO: " + productRequestDTO);
        productMapper.updateEntityFromDto(productRequestDTO, existingProduct);
        existingProduct.setProductId(id);
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toResponseDto(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        productRepository.deleteById(productId);
    }

    @Override
    public Page<ProductResponseDTO> getAllProducts(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new ResourceNotFoundException("Invalid page or size parameters");
        }
        return productRepository.findAll().stream().map(productMapper::toResponseDto).collect(Collectors.collectingAndThen(Collectors.toList(), PageImpl::new));
    }

    @Override
    public ProductResponseDTO getProductByName(String name) {
        Product product = productRepository.findByNameIgnoreCase(name).orElseThrow(() -> new ResourceNotFoundException("Product not found with name: " + name));
        return productMapper.toResponseDto(product);
    }

}

