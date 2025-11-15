package com.supplychainx.integration.service_approvisionnement.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychainx.service_approvisionnement.dto.Request.FournisseurRequestDTO;
import com.supplychainx.service_approvisionnement.dto.Request.MatierePremiereRequestDTO;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long fournisseurId;
    private Long matierePremiereId;

    // Données de test réutilisables
    private static final FournisseurRequestDTO FOURNISSEUR_DTO = createFournisseurDTO();
    private static final MatierePremiereRequestDTO MATIERE_DTO = createMatierePremiereDTO();

    @BeforeEach
    void setUp() throws Exception {
        // Créer un fournisseur une seule fois pour tous les tests
        fournisseurId = createFournisseur();

        // Préparer les données de matière première avec le fournisseur créé
        MATIERE_DTO.setFournisseurIds(Collections.singletonList(fournisseurId));
    }

    @Test
    void testCreateMatierePremiere() throws Exception {
        mockMvc.perform(post("/api/v1/matieres-premieres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(MATIERE_DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Acier"))
                .andExpect(jsonPath("$.data.stockQuantity").value(100))
                .andExpect(jsonPath("$.data.stockMinimum").value(20))
                .andExpect(jsonPath("$.data.unit").value("kg"));
    }

    @Test
    void testFindMatierePremiereById_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/matieres-premieres/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindById_Found() throws Exception {
        // Créer une matière première pour le test
        Long createdMatiereId = createMatierePremiere();

        mockMvc.perform(get("/api/v1/matieres-premieres/" + createdMatiereId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Acier"))
                .andExpect(jsonPath("$.stockQuantity").value(100))
                .andExpect(jsonPath("$.stockMinimum").value(20))
                .andExpect(jsonPath("$.unit").value("kg"));
    }

    @Test
    void testGetAllMatieresPremieres() throws Exception {
        mockMvc.perform(get("/api/v1/matieres-premieres")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void testDeleteMatierePremiere() throws Exception {
        // Créer une matière première pour la supprimer
        Long createdMatiereId = createMatierePremiere();

        mockMvc.perform(delete("/api/v1/matieres-premieres/" + createdMatiereId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Matière premiere deleted successfully"));
    }

    @Test
    void testUpdateMatierePremiere() throws Exception {
        // Créer une matière première pour la modifier
        Long createdMatiereId = createMatierePremiere();

        MatierePremiereRequestDTO updateDTO = createMatierePremiereDTO();
        updateDTO.setName("Aluminium");
        updateDTO.setStockQuantity(150);
        updateDTO.setStockMinimum(30);
        updateDTO.setFournisseurIds(Collections.singletonList(fournisseurId));

        mockMvc.perform(put("/api/v1/matieres-premieres/" + createdMatiereId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Aluminium"))
                .andExpect(jsonPath("$.data.stockQuantity").value(150))
                .andExpect(jsonPath("$.data.stockMinimum").value(30))
                .andExpect(jsonPath("$.data.unit").value("kg"));
    }

    @Test
    void testFiltrerParStockCritique() throws Exception {
        mockMvc.perform(get("/api/v1/matieres-premieres/filtrer-par-stock-critique")
                        .param("stockCritique", "50")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // Méthodes utilitaires
    private Long createFournisseur() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/fournisseurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(FOURNISSEUR_DTO)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode jsonResponse = objectMapper.readTree(result.getResponse().getContentAsString());
        return extractIdFromResponse(jsonResponse);
    }

    private Long createMatierePremiere() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/matieres-premieres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(MATIERE_DTO)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonResponse = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode dataNode = jsonResponse.get("data");
        return extractIdFromResponse(dataNode);
    }

    private Long extractIdFromResponse(JsonNode jsonNode) {
        if (jsonNode.has("matierePremiereId")) {
            return jsonNode.get("matierePremiereId").asLong();
        } else if (jsonNode.has("fournisseurId")) {
            return jsonNode.get("fournisseurId").asLong();
        } else if (jsonNode.has("id")) {
            return jsonNode.get("id").asLong();
        }
        throw new RuntimeException("ID not found in response");
    }

    private static FournisseurRequestDTO createFournisseurDTO() {
        FournisseurRequestDTO dto = new FournisseurRequestDTO();
        dto.setName("Fournisseur Test");
        dto.setContactEmail("test@example.com");
        dto.setPhoneNumber("+1234567890");
        dto.setAddress("123 Rue Test, Ville, Pays");
        dto.setIsActive(true);
        dto.setLeadTimeDays(5);
        dto.setRating(4.0);
        return dto;
    }

    private static MatierePremiereRequestDTO createMatierePremiereDTO() {
        MatierePremiereRequestDTO dto = new MatierePremiereRequestDTO();
        dto.setName("Acier");
        dto.setStockQuantity(100);
        dto.setStockMinimum(20);
        dto.setUnit("kg");
        return dto;
    }
}