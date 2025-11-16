package com.supplychainx.integration.service_production.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychainx.service_approvisionnement.dto.Request.MatierePremiereRequestDTO;
import com.supplychainx.service_approvisionnement.dto.Response.MatierePremiereResponseDTO;
import com.supplychainx.service_approvisionnement.repository.MatierePremiereRepository;
import com.supplychainx.service_approvisionnement.service.MatierePremiereService;
import com.supplychainx.service_production.dto.Request.BillOfMaterialRequestDTO;
import com.supplychainx.service_production.dto.Request.ProductRequestDTO;
import com.supplychainx.service_production.dto.Response.ProductResponseDTO;
import com.supplychainx.service_production.repository.BillOfMaterialRepository;
import com.supplychainx.service_production.repository.ProductRepository;
import com.supplychainx.service_production.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class BillOfMaterielControllerTest {

    @Autowired
    private BillOfMaterialRepository billOfMaterialRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private MatierePremiereService matierePremiereService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MatierePremiereRepository matierePremiereRepository;

    @BeforeEach
    public void setUp() {
        billOfMaterialRepository.deleteAll();   // 1) enfants d’abord
        productRepository.deleteAll();          // 2) parents ensuite
        matierePremiereRepository.deleteAll();  // 3) les autres si besoin
    }

    private ProductResponseDTO createProductResponseDTO() {
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("Test Product");
        dto.setProductionTimeHours(5);
        dto.setCostPerUnit(100.0);
        dto.setStock(50);
        return productService.createProduct(dto);
    }
    private MatierePremiereResponseDTO createMatierePremiereResponseDTO() {
        MatierePremiereRequestDTO dto = new MatierePremiereRequestDTO();
        dto.setName("Test Matiere Premiere");
        dto.setStockMinimum(10);
        dto.setUnit("kg");
        dto.setStockQuantity(100);
        return matierePremiereService.create(dto);
    }
    private BillOfMaterialRequestDTO createBillOfMaterialRequestDTO(Long productId, Long matierePremiereId) {
        BillOfMaterialRequestDTO dto = new BillOfMaterialRequestDTO();
        dto.setProductId(productId);
        dto.setMatierePremiereId(matierePremiereId);
        dto.setQuantity(20);
        return dto;
    }

    @Test
    void testCreateBillOfMaterial() throws Exception{
        ProductResponseDTO product = createProductResponseDTO();
        MatierePremiereResponseDTO matierePremiere = createMatierePremiereResponseDTO();
        BillOfMaterialRequestDTO billOfMaterialRequestDTO = createBillOfMaterialRequestDTO(product.productId(), matierePremiere.matierePremiereId());
        mockMvc.perform(post("/api/v1/bill-of-material")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(billOfMaterialRequestDTO)))
                .andExpect(jsonPath("$.productId").value(product.productId()))
                .andExpect(jsonPath("$.matierePremiereId").value(matierePremiere.matierePremiereId()))
                .andExpect(jsonPath("$.quantity").value(20));
    }

}
