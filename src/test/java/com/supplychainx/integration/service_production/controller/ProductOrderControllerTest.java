package com.supplychainx.integration.service_production.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychainx.service_production.dto.Request.ProductOrderRequestDTO;
import com.supplychainx.service_production.dto.Request.ProductRequestDTO;
import com.supplychainx.service_production.dto.Response.ProductOrderResponseDTO;
import com.supplychainx.service_production.dto.Response.ProductResponseDTO;
import com.supplychainx.service_production.model.enums.ProductionOrderStatus;
import com.supplychainx.service_production.repository.ProductOrderRepository;
import com.supplychainx.service_production.service.ProductOrderService;
import com.supplychainx.service_production.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties",
        properties = {
                "spring.config.location=classpath:application-test.properties",
                "spring.config.name=application-test"
        }
)
public class ProductOrderControllerTest {

    @Autowired
    private ProductOrderRepository productOrderRepository;
    @Autowired
    private ProductOrderService productOrderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        productOrderRepository.deleteAll();
    }

    private ProductRequestDTO createProductRequestDTO() {
        ProductRequestDTO productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName("Sample Product");
        productRequestDTO.setStock(100);
        productRequestDTO.setProductionTimeHours(48);
        productRequestDTO.setCostPerUnit(25.50);
        return productRequestDTO;
    }

    private ProductOrderRequestDTO createProductOrderRequestDTO(Long productId) {
        ProductOrderRequestDTO productOrderRequestDTO = new ProductOrderRequestDTO();
        productOrderRequestDTO.setProductId(productId);
        productOrderRequestDTO.setQuantity(10);
        productOrderRequestDTO.setStatus(ProductionOrderStatus.EN_ATTENTE);
        productOrderRequestDTO.setStartDate(LocalDateTime.now());
        productOrderRequestDTO.setEndDate(LocalDateTime.now().plusDays(2));
        return productOrderRequestDTO;
    }

    @Test
    void testCreateProductOrder() throws Exception {
        ProductResponseDTO productResponseDTO = productService.createProduct(createProductRequestDTO());
        ProductOrderRequestDTO orderRequest = createProductOrderRequestDTO(productResponseDTO.productId());

        mockMvc.perform(post("/api/v1/products-orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(jsonPath("$.productOrderId").exists())
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.status").value("EN_ATTENTE"))
                .andExpect(jsonPath("$.productId").value(productResponseDTO.productId()));
    }

    @Test
    void testFindProductOrderById() throws Exception {
        ProductResponseDTO productResponseDTO = productService.createProduct(createProductRequestDTO());
        ProductOrderRequestDTO orderRequest = createProductOrderRequestDTO(productResponseDTO.productId());
        ProductOrderResponseDTO createdOrder = productOrderService.createProductOrder(orderRequest);

        mockMvc.perform(get("/api/v1/products-orders/" + createdOrder.productOrderId()))
                .andExpect(jsonPath("$.productOrderId").value(createdOrder.productOrderId()))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    void testDeleteProductOrder() throws Exception {
        ProductResponseDTO productResponseDTO = productService.createProduct(createProductRequestDTO());
        ProductOrderRequestDTO orderRequest = createProductOrderRequestDTO(productResponseDTO.productId());
        ProductOrderResponseDTO createdOrder = productOrderService.createProductOrder(orderRequest);

        mockMvc.perform(delete("/api/v1/products-orders/" + createdOrder.productOrderId()))
                .andExpect(jsonPath("$.message").value("Product order deleted successfully"));
    }

    @Test
    void testUpdateProductOrder() throws Exception {
        ProductResponseDTO productResponseDTO = productService.createProduct(createProductRequestDTO());
        ProductOrderRequestDTO orderRequest = createProductOrderRequestDTO(productResponseDTO.productId());
        ProductOrderResponseDTO createdOrder = productOrderService.createProductOrder(orderRequest);

        orderRequest.setQuantity(20);

        mockMvc.perform(put("/api/v1/products-orders/" + createdOrder.productOrderId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(jsonPath("$.productOrderId").value(createdOrder.productOrderId()))
                .andExpect(jsonPath("$.quantity").value(20));
    }

    @Test
    void testGetAllProductOrders() throws Exception {
        ProductResponseDTO productResponseDTO = productService.createProduct(createProductRequestDTO());
        ProductOrderRequestDTO orderRequest1 = createProductOrderRequestDTO(productResponseDTO.productId());
        ProductOrderRequestDTO orderRequest2 = createProductOrderRequestDTO(productResponseDTO.productId());
        productOrderService.createProductOrder(orderRequest1);
        productOrderService.createProductOrder(orderRequest2);

        mockMvc.perform(get("/api/v1/products-orders?page=0&size=10"))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void testStartProductOrder() throws Exception {
        ProductResponseDTO productResponseDTO = productService.createProduct(createProductRequestDTO());
        ProductOrderRequestDTO orderRequest = createProductOrderRequestDTO(productResponseDTO.productId());
        ProductOrderResponseDTO createdOrder = productOrderService.createProductOrder(orderRequest);

        mockMvc.perform(put("/api/v1/products-orders/" + createdOrder.productOrderId() + "/start"))
                .andExpect(jsonPath("$.data.productOrderId").value(createdOrder.productOrderId()))
                .andExpect(jsonPath("$.data.status").value(ProductionOrderStatus.EN_PRODUCTION.name()));
    }

    @Test
    void testCompleteProductOrder() throws Exception {
        ProductResponseDTO productResponseDTO = productService.createProduct(createProductRequestDTO());
        ProductOrderRequestDTO orderRequest = createProductOrderRequestDTO(productResponseDTO.productId());
        ProductOrderResponseDTO createdOrder = productOrderService.createProductOrder(orderRequest);

        // Start the order first
        productOrderService.startProduction(createdOrder.productOrderId());

        mockMvc.perform(put("/api/v1/products-orders/" + createdOrder.productOrderId() + "/complete"))
                .andExpect(jsonPath("$.data.productOrderId").value(createdOrder.productOrderId()))
                .andExpect(jsonPath("$.data.status").value(ProductionOrderStatus.TERMINE.name()));
    }

    @Test
    void testCancelProductOrder() throws Exception {
        ProductResponseDTO productResponseDTO = productService.createProduct(createProductRequestDTO());
        ProductOrderRequestDTO orderRequest = createProductOrderRequestDTO(productResponseDTO.productId());
        ProductOrderResponseDTO createdOrder = productOrderService.createProductOrder(orderRequest);

        mockMvc.perform(put("/api/v1/products-orders/" + createdOrder.productOrderId() + "/cancel"))
                .andExpect(jsonPath("$.data.productOrderId").value(createdOrder.productOrderId()))
                .andExpect(jsonPath("$.data.status").value(ProductionOrderStatus.BLOQUEE.name()));
    }

}
