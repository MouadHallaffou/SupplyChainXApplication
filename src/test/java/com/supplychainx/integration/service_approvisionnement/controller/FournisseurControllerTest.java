// java
package com.supplychainx.integration.service_approvisionnement.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychainx.service_approvisionnement.dto.Request.FournisseurRequestDTO;
import com.supplychainx.service_approvisionnement.repository.FournisseurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
                "spring.config.location=classpath:application-test.properties",
                "spring.config.name=application-test"
        }
)
public class FournisseurControllerTest {

    private static final String BASE_URL = "/api/v1/fournisseurs";

    @Autowired
    private FournisseurRepository fournisseurRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        fournisseurRepository.deleteAll();
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fournisseur A"));
    }

    @Test
    void testGetFournisseurById() throws Exception {
        long fournisseurId = createFournisseurAndGetId(buildFournisseur("Fournisseur A", "fr@gmail.com", "1234567890", true));

        mockMvc.perform(get(BASE_URL + "/" + fournisseurId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fournisseurId").value(fournisseurId))
                .andExpect(jsonPath("$.name").value("Fournisseur A"));
    }

    @Test
    void testGetAllFournisseurs() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    void testDeleteFournisseur() throws Exception {
        long fournisseurId = createFournisseurAndGetId(buildFournisseur("Fournisseur A", "fr@gmail.com", "1234567890", true));

        mockMvc.perform(delete(BASE_URL + "/" + fournisseurId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Fournisseur deleted successfully"));
    }

    @Test
    void testUpdateFournisseur() throws Exception {
        long fournisseurId = createFournisseurAndGetId(buildFournisseur("Fournisseur A", "fr@gmail.com", "1234567890", true));

        FournisseurRequestDTO updated = buildFournisseur("Fournisseur B", "fr@gmail.com", "1234567822", false);

        mockMvc.perform(put(BASE_URL + "/" + fournisseurId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fournisseur B"))
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void testSearchFournisseurByName() throws Exception {
        createFournisseurAndGetId(buildFournisseur("Fournisseur A", "fr@gmail.com", "1234567890", true));

        mockMvc.perform(get(BASE_URL + "/search").param("name", "Fournisseur A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fournisseur A"))
                .andExpect(jsonPath("$.contactEmail").value("fr@gmail.com"));
    }
}
