package com.supplychainx.integration.service_production.repository;

import com.supplychainx.service_production.model.Product;
import com.supplychainx.service_production.repository.ProductOrderRepository;
import com.supplychainx.service_production.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(
        locations = "classpath:application-test.properties",
        properties = {
                "spring.config.location=classpath:application-test.properties",
                "spring.config.name=application-test"
        }
)

public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductOrderRepository productOrderRepository;

    @BeforeEach
    void setUp() {
        productOrderRepository.deleteAll();
        productRepository.deleteAll();
    }

    private Product createProduct() {
        Product product = new Product();
        product.setName("Produit Test");
        product.setStock(500);
        product.setProductionTimeHours(48);
        product.setCostPerUnit(15.75);
        return product;
    }

    @Test
    void testSaveProduct() {
        Product product = createProduct();
        Product savedProduct = productRepository.save(product);
        assert savedProduct.getProductId() != null;
        assert savedProduct.getName().equals("Produit Test");
    }

    @Test
    void testFindProductById() {
        Product product = createProduct();
        Product savedProduct = productRepository.save(product);
        Product foundProduct = productRepository.findById(savedProduct.getProductId()).orElse(null);
        assert foundProduct != null;
        assert foundProduct.getName().equals("Produit Test");
    }

    @Test
    void testDeleteProduct() {
        Product product = createProduct();
        Product savedProduct = productRepository.save(product);
        productRepository.deleteById(savedProduct.getProductId());
        Product foundProduct = productRepository.findById(savedProduct.getProductId()).orElse(null);
        assert foundProduct == null;
    }

    @Test
    void testUpdateProduct() {
        Product product = createProduct();
        Product savedProduct = productRepository.save(product);
        savedProduct.setStock(300);
        Product updatedProduct = productRepository.save(savedProduct);
        assert updatedProduct.getStock() == 300;
    }

    @Test
    void testFindAllProducts() {
        Product product1 = createProduct();
        Product product2 = createProduct();
        product2.setName("Produit Test 2");
        productRepository.save(product1);
        productRepository.save(product2);
        Iterable<Product> products = productRepository.findAll();
        int count = 0;
        for (Product p : products) {
            count++;
        }
        assert count == 2;
    }

    @Test
    void testFinfByNameIgnoreCase() {
        Product product = new Product();
        product.setName("Produit Unique");
        product.setStock(100);
        product.setProductionTimeHours(24);
        product.setCostPerUnit(20.00);
        productRepository.save(product);
        Product foundProduct = productRepository.findByNameIgnoreCase("produit unique").orElse(null);
        assert foundProduct != null;
        assert foundProduct.getName().equals("Produit Unique");
    }

}
