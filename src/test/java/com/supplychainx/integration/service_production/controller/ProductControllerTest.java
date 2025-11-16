package com.supplychainx.integration.service_production.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychainx.service_approvisionnement.repository.MatierePremiereRepository;
import com.supplychainx.service_production.dto.Request.ProductRequestDTO;
import com.supplychainx.service_production.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties",
        properties = {
                "spring.config.location=classpath:application-test.properties",
                "spring.config.name=application-test"
        }
)
public class ProductControllerTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MatierePremiereRepository matierePremiereRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        productRepository.deleteAll();
        matierePremiereRepository.deleteAll();
    }

    private ProductRequestDTO createProductRequestDTO() {
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("Test Product");
        dto.setProductionTimeHours(5);
        dto.setCostPerUnit(100.0);
        dto.setStock(50);
        return dto;
    }

    @Test
    public void testCreateProduct() throws Exception{
        mockMvc.perform(post("/api/v1/products")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(createProductRequestDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.productionTimeHours").value(5))
                .andExpect(jsonPath("$.costPerUnit").value(100.0))
                .andExpect(jsonPath("$.stock").value(50));
    }

    @Test
    public void testGetProductById() throws Exception {
        String response = mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createProductRequestDTO())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(response);
        long productId = root.hasNonNull("id") ? root.get("id").asLong() : root.path("productId").asLong();

        mockMvc.perform(get("/api/v1/products/"+ productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId));
    }

    @Test
    void testDeleteProduct() throws Exception {
        String response = mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createProductRequestDTO())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(response);
        long productId = root.hasNonNull("id") ? root.get("id").asLong() : root.path("productId").asLong();

        mockMvc.perform(delete("/api/v1/products/" + productId))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testUpdateProduct() throws Exception {
        String response = mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createProductRequestDTO())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(response);
        long productId = root.hasNonNull("id") ? root.get("id").asLong() : root.path("productId").asLong();

        ProductRequestDTO updatedDTO = createProductRequestDTO();
        updatedDTO.setName("Updated Product");
        updatedDTO.setProductionTimeHours(10);
        updatedDTO.setCostPerUnit(150.0);
        updatedDTO.setStock(75);

        mockMvc.perform(put("/api/v1/products/" + productId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.productionTimeHours").value(10))
                .andExpect(jsonPath("$.costPerUnit").value(150.0))
                .andExpect(jsonPath("$.stock").value(75));
    }

    @Test
    void testGetAllProducts() throws Exception {
        for (int i = 0; i < 15; i++) {
            mockMvc.perform(post("/api/v1/products")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(createProductRequestDTO())))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/api/v1/products?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void testGetProductByName() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createProductRequestDTO())))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/products/search?name=Test Product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

}
