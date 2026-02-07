package com.supplychainx.integration.service_production.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychainx.security.auth.dto.LoginRequestDto;
import com.supplychainx.service_approvisionnement.dto.Request.MatierePremiereRequestDTO;
import com.supplychainx.service_approvisionnement.dto.Response.MatierePremiereResponseDTO;
import com.supplychainx.service_approvisionnement.service.MatierePremiereService;
import com.supplychainx.service_production.dto.Request.BillOfMaterialRequestDTO;
import com.supplychainx.service_production.dto.Request.ProductRequestDTO;
import com.supplychainx.service_production.dto.Response.BillOfMaterialResponseDTO;
import com.supplychainx.service_production.dto.Response.ProductResponseDTO;
import com.supplychainx.service_production.service.BillOfMaterialService;
import com.supplychainx.service_production.service.ProductService;
import com.supplychainx.service_user.model.Role;
import com.supplychainx.service_user.model.User;
import com.supplychainx.service_user.repository.RoleRepository;
import com.supplychainx.service_user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties",
        properties = {
                "jwt.secret=TEST_SECRET_KEY_256_BITS_MINIMUM_FOR_JJWT",
                "jwt.expiration-ms=3600000",
                "jwt.refresh-expiration-ms=86400000",
                "spring.config.location=classpath:application-test.properties",
                "spring.config.name=application-test"
        }
)
public class BillOfMaterielControllerTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private MatierePremiereService matierePremiereService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BillOfMaterialService billOfMaterialService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("DELETE FROM bill_of_materials");
        jdbcTemplate.execute("DELETE FROM product_orders");
        jdbcTemplate.execute("DELETE FROM products");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM roles");

        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole = roleRepository.save(adminRole);

        User admin = new User();
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFirstName("Admin");
        admin.setLastName("Test");
        admin.setRole(adminRole);
        admin.setIsActive(true);
        userRepository.save(admin);
    }

    private String loginAndGetToken() throws Exception {
        LoginRequestDto login = LoginRequestDto.builder()
                .email("admin@gmail.com")
                .password("admin123")
                .build();

        MvcResult result = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(login))
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
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
    void testCreateBillOfMaterial() throws Exception {
        ProductResponseDTO product = createProductResponseDTO();
        MatierePremiereResponseDTO matierePremiere = createMatierePremiereResponseDTO();
        BillOfMaterialRequestDTO billOfMaterialRequestDTO = createBillOfMaterialRequestDTO(product.productId(), matierePremiere.matierePremiereId());
        mockMvc.perform(post("/api/v1/bill-of-material")
                        .header("Authorization", "Bearer " + loginAndGetToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(billOfMaterialRequestDTO)))
                .andExpect(jsonPath("$.productId").value(product.productId()))
                .andExpect(jsonPath("$.matierePremiereId").value(matierePremiere.matierePremiereId()))
                .andExpect(jsonPath("$.quantity").value(20));
    }

    @Test
    void testFindById() throws Exception {
        ProductResponseDTO product = createProductResponseDTO();
        MatierePremiereResponseDTO matierePremiere = createMatierePremiereResponseDTO();
        BillOfMaterialRequestDTO billOfMaterialRequestDTO = createBillOfMaterialRequestDTO(product.productId(), matierePremiere.matierePremiereId());
        BillOfMaterialResponseDTO savedBillOfMaterial = billOfMaterialService.createBillOfMaterial(billOfMaterialRequestDTO);

        mockMvc.perform(get("/api/v1/bill-of-material/" + savedBillOfMaterial.bomId())
                        .header("Authorization", "Bearer " + loginAndGetToken())
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bomId").value(savedBillOfMaterial.bomId()))
                .andExpect(jsonPath("$.productId").value(product.productId()))
                .andExpect(jsonPath("$.matierePremiereId").value(matierePremiere.matierePremiereId()))
                .andExpect(jsonPath("$.quantity").value(20));
    }

    @Test
    void testUpdateBillOfMaterial() throws Exception {
        ProductResponseDTO product = createProductResponseDTO();
        MatierePremiereResponseDTO matierePremiere = createMatierePremiereResponseDTO();
        BillOfMaterialRequestDTO billOfMaterialRequestDTO = createBillOfMaterialRequestDTO(product.productId(), matierePremiere.matierePremiereId());
        BillOfMaterialResponseDTO savedBillOfMaterial = billOfMaterialService.createBillOfMaterial(billOfMaterialRequestDTO);

        BillOfMaterialRequestDTO updateDTO = new BillOfMaterialRequestDTO();
        updateDTO.setProductId(product.productId());
        updateDTO.setMatierePremiereId(matierePremiere.matierePremiereId());
        updateDTO.setQuantity(50);

        mockMvc.perform(put("/api/v1/bill-of-material/" + savedBillOfMaterial.bomId())
                        .header("Authorization", "Bearer " + loginAndGetToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(jsonPath("$.bomId").value(savedBillOfMaterial.bomId()))
                .andExpect(jsonPath("$.quantity").value(50));
    }

    @Test
    void testDeleteBillOfMaterial() throws Exception {
        ProductResponseDTO product = createProductResponseDTO();
        MatierePremiereResponseDTO matierePremiere = createMatierePremiereResponseDTO();
        BillOfMaterialRequestDTO billOfMaterialRequestDTO = createBillOfMaterialRequestDTO(product.productId(), matierePremiere.matierePremiereId());
        BillOfMaterialResponseDTO savedBillOfMaterial = billOfMaterialService.createBillOfMaterial(billOfMaterialRequestDTO);

        mockMvc.perform(delete("/api/v1/bill-of-material/" + savedBillOfMaterial.bomId())
                        .header("Authorization", "Bearer " + loginAndGetToken())
                        .contentType("application/json"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetAllBillOfMaterial() throws Exception {
        ProductResponseDTO product = createProductResponseDTO();
        MatierePremiereResponseDTO matierePremiere = createMatierePremiereResponseDTO();
        BillOfMaterialRequestDTO billOfMaterialRequestDTO = createBillOfMaterialRequestDTO(product.productId(), matierePremiere.matierePremiereId());
        billOfMaterialService.createBillOfMaterial(billOfMaterialRequestDTO);

        mockMvc.perform(get("/api/v1/bill-of-material?page=0&size=10")
                        .header("Authorization", "Bearer " + loginAndGetToken())
                        .contentType("application/json"))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].productId").value(product.productId()))
                .andExpect(jsonPath("$.content[0].matierePremiereId").value(matierePremiere.matierePremiereId()))
                .andExpect(jsonPath("$.content[0].quantity").value(20));
    }

}
