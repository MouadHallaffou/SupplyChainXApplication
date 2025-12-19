package com.supplychainx.integration.service_approvisionnement.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychainx.security.auth.dto.LoginRequestDto;
import com.supplychainx.service_approvisionnement.dto.Request.CommandeFournisseurMatiereRequestDTO;
import com.supplychainx.service_approvisionnement.dto.Request.CommandeFournisseurRequestDTO;
import com.supplychainx.service_approvisionnement.dto.Request.FournisseurRequestDTO;
import com.supplychainx.service_approvisionnement.dto.Request.MatierePremiereRequestDTO;
import com.supplychainx.service_approvisionnement.model.enums.FournisseurOrderStatus;
import com.supplychainx.service_approvisionnement.repository.CommandeFournisseurRepository;
import com.supplychainx.service_approvisionnement.repository.FournisseurRepository;
import com.supplychainx.service_approvisionnement.repository.MatierePremiereRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
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
public class OrderControllerTest {

    @Autowired
    private MatierePremiereRepository matierePremiereRepository;
    @Autowired
    private FournisseurRepository fournisseurRepository;
    @Autowired
    private CommandeFournisseurRepository commandeFournisseurRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        matierePremiereRepository.deleteAll();
        fournisseurRepository.deleteAll();
        commandeFournisseurRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
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

    private CommandeFournisseurMatiereRequestDTO createCommandeFournisseurMatiere(long matiereId, int quantite) {
        CommandeFournisseurMatiereRequestDTO dto = new CommandeFournisseurMatiereRequestDTO();
        dto.setMatierePremiereId(matiereId);
        dto.setQuantite(quantite);
        return dto;
    }

    private long createFournisseur() throws Exception {
        FournisseurRequestDTO fournisseurRequestDTO = new FournisseurRequestDTO();
        fournisseurRequestDTO.setName("Fournisseur1");
        fournisseurRequestDTO.setContactEmail("fr@hotmail.go");
        fournisseurRequestDTO.setPhoneNumber("+1234567890");
        fournisseurRequestDTO.setAddress("123 , khenifra , maroc");
        fournisseurRequestDTO.setIsActive(true);
        fournisseurRequestDTO.setLeadTimeDays(7);
        fournisseurRequestDTO.setRating(4.5);

        MvcResult result = mockMvc.perform(
                        post("/api/v1/fournisseurs")
                                .header("Authorization", "Bearer " + loginAndGetToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(fournisseurRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.has("fournisseurId") ? json.get("fournisseurId").asLong() : json.get("id").asLong();
    }

    private long createMatiere(long fournisseurId) throws Exception {
        MatierePremiereRequestDTO requestDTO = new MatierePremiereRequestDTO();
        requestDTO.setName("Acier");
        requestDTO.setStockQuantity(100);
        requestDTO.setStockMinimum(20);
        requestDTO.setUnit("kg");
        requestDTO.setFournisseurIds(Collections.singletonList(fournisseurId));

        MvcResult result = mockMvc.perform(
                        post("/api/v1/matieres-premieres")
                                .header("Authorization", "Bearer " + loginAndGetToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode data = json.has("data") ? json.get("data") : json;
        return data.has("matierePremiereId") ? data.get("matierePremiereId").asLong() : data.get("id").asLong();
    }

    private long createOrder(long fournisseurId, long matiereId) throws Exception {
        CommandeFournisseurRequestDTO orderRequestDTO = new CommandeFournisseurRequestDTO();
        orderRequestDTO.setFournisseurId(fournisseurId);
        orderRequestDTO.setOrderDate(LocalDate.now());
        orderRequestDTO.setCommandeFournisseurMatieres(
                Collections.singletonList(createCommandeFournisseurMatiere(matiereId, 50))
        );
        orderRequestDTO.setStatus(FournisseurOrderStatus.EN_ATTENTE);

        MvcResult result = mockMvc.perform(
                        post("/api/v1/commande-fournisseurs")
                                .header("Authorization", "Bearer " + loginAndGetToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.has("data")
                ? json.get("data").get("orderFournisseurId").asLong()
                : json.get("orderFournisseurId").asLong();
    }

    @Test
    void testCreateOrder() throws Exception {
        long fournisseurId = createFournisseur();
        long matiereId = createMatiere(fournisseurId);
        long orderId = createOrder(fournisseurId, matiereId);

        mockMvc.perform(get("/api/v1/commande-fournisseurs/" + orderId)
                        .header("Authorization", "Bearer " + loginAndGetToken()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetOrderById() throws Exception {
        long fournisseurId = createFournisseur();
        long matiereId = createMatiere(fournisseurId);
        long orderId = createOrder(fournisseurId, matiereId);

        mockMvc.perform(get("/api/v1/commande-fournisseurs/" + orderId)
                        .header("Authorization", "Bearer " + loginAndGetToken()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllOrders() throws Exception {
        mockMvc.perform(get("/api/v1/commande-fournisseurs")
                        .header("Authorization", "Bearer " + loginAndGetToken())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray());
    }

    @Test
    void testDeleteOrder() throws Exception {
        long fournisseurId = createFournisseur();
        long matiereId = createMatiere(fournisseurId);
        long orderId = createOrder(fournisseurId, matiereId);

        mockMvc.perform(delete("/api/v1/commande-fournisseurs/" + orderId)
                        .header("Authorization", "Bearer " + loginAndGetToken()))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(status().isOk());
    }

    // ### start order processing (en cours)
    @Test
    void testStartOrderProcessing() throws Exception {
        long fournisseurId = createFournisseur();
        long matiereId = createMatiere(fournisseurId);
        long orderId = createOrder(fournisseurId, matiereId);

        mockMvc.perform(put("/api/v1/commande-fournisseurs/" + orderId + "/start")
                        .header("Authorization", "Bearer " + loginAndGetToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(FournisseurOrderStatus.EN_COURS.name()));
    }

    // ### complete order processing (recue)
    @Test
    void testCompleteOrderProcessing() throws Exception {
        long fournisseurId = createFournisseur();
        long matiereId = createMatiere(fournisseurId);
        long orderId = createOrder(fournisseurId, matiereId);

        mockMvc.perform(put("/api/v1/commande-fournisseurs/" + orderId + "/complete")
                        .header("Authorization", "Bearer " + loginAndGetToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(FournisseurOrderStatus.RECUE.name()));
    }

    // ### cancel order (annulee)
    @Test
    void testCancelOrder() throws Exception {
        long fournisseurId = createFournisseur();
        long matiereId = createMatiere(fournisseurId);
        long orderId = createOrder(fournisseurId, matiereId);

        mockMvc.perform(put("/api/v1/commande-fournisseurs/" + orderId + "/cancel")
                        .header("Authorization", "Bearer " + loginAndGetToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(FournisseurOrderStatus.ANNULEE.name()));
    }

}
