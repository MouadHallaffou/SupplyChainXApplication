// java
package com.supplychainx.integration.service_approvisionnement.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychainx.service_approvisionnement.dto.Request.FournisseurRequestDTO;
import com.supplychainx.service_approvisionnement.dto.Request.MatierePremiereRequestDTO;
import com.supplychainx.service_approvisionnement.repository.FournisseurRepository;
import com.supplychainx.service_approvisionnement.repository.MatierePremiereRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

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
public class MatierePremiereControllerTest {

    private static final String BASE_URL_MAT = "/api/v1/matieres-premieres";
    private static final String BASE_URL_FOUR = "/api/v1/fournisseurs";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MatierePremiereRepository matierePremiereRepository;
    @Autowired
    private FournisseurRepository fournisseurRepository;

    @BeforeEach
    void setUp() {
        matierePremiereRepository.deleteAll();
        fournisseurRepository.deleteAll();
    }

    private FournisseurRequestDTO buildFournisseur(String name, boolean active) {
        FournisseurRequestDTO dto = new FournisseurRequestDTO();
        dto.setName(name);
        dto.setContactEmail("fr@hotmail.go");
        dto.setPhoneNumber("+1234567890");
        dto.setAddress("123 Rue Principale, Ville, Pays");
        dto.setIsActive(active);
        dto.setLeadTimeDays(7);
        dto.setRating(4.5);
        return dto;
    }

    private MatierePremiereRequestDTO buildMatiere(String name, long fournisseurId, int stockQ, int stockMin, String unit) {
        MatierePremiereRequestDTO dto = new MatierePremiereRequestDTO();
        dto.setName(name);
        dto.setStockQuantity(stockQ);
        dto.setStockMinimum(stockMin);
        dto.setUnit(unit);
        dto.setFournisseurIds(Collections.singletonList(fournisseurId));
        return dto;
    }

    private JsonNode toJson(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private long createFournisseurAndGetId(FournisseurRequestDTO dto) throws Exception {
        MvcResult res = mockMvc.perform(post(BASE_URL_FOUR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode json = toJson(res);
        return json.has("fournisseurId") ? json.get("fournisseurId").asLong() : json.get("id").asLong();
    }

    private long createMatiereAndGetId(MatierePremiereRequestDTO dto) throws Exception {
        MvcResult res = mockMvc.perform(post(BASE_URL_MAT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode data = toJson(res).get("data");
        return data.has("matierePremiereId") ? data.get("matierePremiereId").asLong() : data.get("id").asLong();
    }

    @Test
    void testCreateMatierePremiere() throws Exception {
        long fournisseurId = createFournisseurAndGetId(buildFournisseur("Fournisseur1", true));
        MatierePremiereRequestDTO requestDTO = buildMatiere("Acier", fournisseurId, 100, 20, "kg");

        mockMvc.perform(post(BASE_URL_MAT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Acier"))
                .andExpect(jsonPath("$.data.stockQuantity").value(100))
                .andExpect(jsonPath("$.data.stockMinimum").value(20))
                .andExpect(jsonPath("$.data.unit").value("kg"));
    }

    @Test
    void testFindById_Found() throws Exception {
        long fournisseurId = createFournisseurAndGetId(buildFournisseur("Fournisseur1", true));
        long matiereId = createMatiereAndGetId(buildMatiere("Acier", fournisseurId, 100, 20, "kg"));

        mockMvc.perform(get(BASE_URL_MAT + "/" + matiereId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Acier"))
                .andExpect(jsonPath("$.stockQuantity").value(100))
                .andExpect(jsonPath("$.stockMinimum").value(20))
                .andExpect(jsonPath("$.unit").value("kg"));
    }

    @Test
    void testGetAllMatieresPremieres() throws Exception {
        mockMvc.perform(get(BASE_URL_MAT).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void testDeleteMatierePremiere() throws Exception {
        long fournisseurId = createFournisseurAndGetId(buildFournisseur("Fournisseur1", true));
        long matiereId = createMatiereAndGetId(buildMatiere("Acier", fournisseurId, 100, 20, "kg"));

        mockMvc.perform(delete(BASE_URL_MAT + "/" + matiereId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testUpdateMatierePremiere() throws Exception {
        long fournisseurId = createFournisseurAndGetId(buildFournisseur("Fournisseur1", true));
        long matiereId = createMatiereAndGetId(buildMatiere("Acier", fournisseurId, 100, 20, "kg"));

        MatierePremiereRequestDTO updateDTO = buildMatiere("Aluminium", fournisseurId, 150, 30, "kg");

        mockMvc.perform(put(BASE_URL_MAT + "/" + matiereId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Aluminium"))
                .andExpect(jsonPath("$.data.stockQuantity").value(150))
                .andExpect(jsonPath("$.data.stockMinimum").value(30))
                .andExpect(jsonPath("$.data.unit").value("kg"));
    }

    @Test
    void testFilterByStockMinimum() throws Exception {
        mockMvc.perform(get(BASE_URL_MAT + "/filtrer-par-stock-critique")
                        .param("stockCritique", "50")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

}
