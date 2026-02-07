// java
package com.supplychainx.integration.service_approvisionnement.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychainx.security.auth.dto.LoginRequestDto;
import com.supplychainx.service_approvisionnement.dto.Request.FournisseurRequestDTO;
import com.supplychainx.service_user.model.Role;
import com.supplychainx.service_user.model.User;
import com.supplychainx.service_user.repository.RoleRepository;
import com.supplychainx.service_user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)

public class FournisseurControllerTest {

    private static final String BASE_URL = "/api/v1/fournisseurs";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
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
        jdbcTemplate.execute("DELETE FROM matiere_fournisseur");
        jdbcTemplate.execute("DELETE FROM commande_fournisseur_matiere");
        jdbcTemplate.execute("DELETE FROM commande_fournisseur");
        jdbcTemplate.execute("DELETE FROM fournisseurs");
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

    private FournisseurRequestDTO buildFournisseur(String name, String email, String phone, boolean isActive) {
        FournisseurRequestDTO dto = new FournisseurRequestDTO();
        dto.setName(name);
        dto.setContactEmail(email);
        dto.setPhoneNumber(phone);
        dto.setAddress("123 , khenifra , maroc");
        dto.setRating(1.1);
        dto.setIsActive(isActive);
        dto.setLeadTimeDays(2);
        return dto;
    }

    private long createFournisseurAndGetId(FournisseurRequestDTO dto) throws Exception {
        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + loginAndGetToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        return extractFournisseurId(result.getResponse().getContentAsString());
    }

    private long extractFournisseurId(String content) throws Exception {
        JsonNode json = objectMapper.readTree(content);
        return json.has("fournisseurId") ? json.get("fournisseurId").asLong() : json.get("id").asLong();
    }

    @Test
    void testCreateFournisseur() throws Exception {
        FournisseurRequestDTO dto = buildFournisseur("Fournisseur A", "fr@gmail.com", "1234567890", true);

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + loginAndGetToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fournisseur A"));
    }

    @Test
    void testGetFournisseurById() throws Exception {
        long fournisseurId = createFournisseurAndGetId(buildFournisseur("Fournisseur A", "fr@gmail.com", "1234567890", true));

        mockMvc.perform(get(BASE_URL + "/" + fournisseurId)
                        .header("Authorization", "Bearer " + loginAndGetToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fournisseurId").value(fournisseurId))
                .andExpect(jsonPath("$.name").value("Fournisseur A"));
    }

    @Test
    void testGetAllFournisseurs() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + loginAndGetToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    void testDeleteFournisseur() throws Exception {
        long fournisseurId = createFournisseurAndGetId(buildFournisseur("Fournisseur A", "fr@gmail.com", "1234567890", true));

        mockMvc.perform(delete(BASE_URL + "/" + fournisseurId)
                        .header("Authorization", "Bearer " + loginAndGetToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Fournisseur deleted successfully"));
    }

    @Test
    void testUpdateFournisseur() throws Exception {
        long fournisseurId = createFournisseurAndGetId(buildFournisseur("Fournisseur A", "fr@gmail.com", "1234567890", true));

        FournisseurRequestDTO updated = buildFournisseur("Fournisseur B", "fr@gmail.com", "1234567822", false);

        mockMvc.perform(put(BASE_URL + "/" + fournisseurId)
                        .header("Authorization", "Bearer " + loginAndGetToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fournisseur B"))
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void testSearchFournisseurByName() throws Exception {
        createFournisseurAndGetId(buildFournisseur("Fournisseur A", "fr@gmail.com", "1234567890", true));

        mockMvc.perform(get(BASE_URL + "/search").param("name", "Fournisseur A")
                        .header("Authorization", "Bearer " + loginAndGetToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fournisseur A"))
                .andExpect(jsonPath("$.contactEmail").value("fr@gmail.com"));
    }

}
