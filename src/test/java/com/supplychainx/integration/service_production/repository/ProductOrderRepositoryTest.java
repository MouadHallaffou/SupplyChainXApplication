package com.supplychainx.integration.service_production.repository;

import com.supplychainx.service_approvisionnement.model.MatierePremiere;
import com.supplychainx.service_approvisionnement.repository.MatierePremiereRepository;
import com.supplychainx.service_production.model.BillOfMaterial;
import com.supplychainx.service_production.model.Product;
import com.supplychainx.service_production.model.ProductOrder;
import com.supplychainx.service_production.model.enums.ProductionOrderStatus;
import com.supplychainx.service_production.repository.BillOfMaterialRepository;
import com.supplychainx.service_production.repository.ProductOrderRepository;
import com.supplychainx.service_production.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

@DataJpaTest
@TestPropertySource(
        locations = "classpath:application-test.properties",
        properties = {
                "spring.config.location=classpath:application-test.properties",
                "spring.config.name=application-test"
        }
)
public class ProductOrderRepositoryTest {

    @Autowired
    private ProductOrderRepository productOrderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MatierePremiereRepository matierePremiereRepository;
    @Autowired
    private BillOfMaterialRepository billOfMaterialRepository;

    @BeforeEach
    void setUp() {
        productOrderRepository.deleteAll();
        productRepository.deleteAll();
        matierePremiereRepository.deleteAll();
        productRepository.deleteAll();
    }
    private Product createAndSaveProduct(String name) {
        Product product = new Product();
        product.setName(name);
        product.setStock(500);
        product.setProductionTimeHours(48);
        product.setCostPerUnit(15.75);
        return productRepository.save(product);
    }

    private ProductOrder createProductOrder(Product product, int orderQuantity) {
        ProductOrder order = new ProductOrder();
        order.setProduct(product);
        order.setQuantity(orderQuantity);
        order.setStartDate(LocalDateTime.now());
        order.setEndDate(LocalDateTime.now().plusDays(7));
        order.setStatus(ProductionOrderStatus.EN_PRODUCTION);
        return order;
    }

    @Test
    void testSaveProductOrder() {
        Product product = createAndSaveProduct("Produit Test");
        ProductOrder order = createProductOrder(product, 100);
        ProductOrder savedOrder = productOrderRepository.save(order);
        assert savedOrder.getProductOrderId() != null;
        assert savedOrder.getQuantity() == 100;
    }

    @Test
    void testFindProductOrderById() {
        Product product = createAndSaveProduct("Produit Test");
        ProductOrder order = createProductOrder(product, 100);
        ProductOrder savedOrder = productOrderRepository.save(order);
        ProductOrder foundOrder = productOrderRepository.findById(savedOrder.getProductOrderId()).orElse(null);
        assert foundOrder != null;
        assert foundOrder.getQuantity() == 100;
    }

    @Test
    void testDeleteProductOrder() {
        Product product = createAndSaveProduct("Produit Test");
        ProductOrder order = createProductOrder(product, 100);
        ProductOrder savedOrder = productOrderRepository.save(order);
        productOrderRepository.deleteById(savedOrder.getProductOrderId());
        ProductOrder foundOrder = productOrderRepository.findById(savedOrder.getProductOrderId()).orElse(null);
        assert foundOrder == null;
    }

    @Test
    void testUpdateProductOrder() {
        Product product = createAndSaveProduct("Produit Test");
        ProductOrder order = createProductOrder(product, 100);
        ProductOrder savedOrder = productOrderRepository.save(order);
        savedOrder.setQuantity(200);
        ProductOrder updatedOrder = productOrderRepository.save(savedOrder);
        assert updatedOrder.getQuantity() == 200;
    }

    @Test
    void testFindAllProductOrders() {
        Product product1 = createAndSaveProduct("Produit Test 1");
        Product product2 = createAndSaveProduct("Produit Test 2");
        ProductOrder order1 = createProductOrder(product1, 100);
        ProductOrder order2 = createProductOrder(product2, 150);
        productOrderRepository.save(order1);
        productOrderRepository.save(order2);
        Iterable<ProductOrder> orders = productOrderRepository.findAll();
        int count = 0;
        for (ProductOrder o : orders) {
            count++;
        }
        assert count == 2;
    }

}
