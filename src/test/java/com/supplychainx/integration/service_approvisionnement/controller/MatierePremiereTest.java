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

public class MatierePremiereTest {

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

    @Test
    void testCreateMatierePremiere() throws Exception {
        FournisseurRequestDTO fournisseurRequestDTO = new FournisseurRequestDTO();
        fournisseurRequestDTO.setName("Fournisseur1");
        fournisseurRequestDTO.setContactEmail("fr@hotmail.go");
        fournisseurRequestDTO.setPhoneNumber("+1234567890");
        fournisseurRequestDTO.setAddress("123 Rue Principale, Ville, Pays");
        fournisseurRequestDTO.setIsActive(true);
        fournisseurRequestDTO.setLeadTimeDays(7);
        fournisseurRequestDTO.setRating(4.5);

        MvcResult fournisseurResult = mockMvc.perform(post("/api/v1/fournisseurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fournisseurRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode fournisseurJson = objectMapper.readTree(fournisseurResult.getResponse().getContentAsString());
        long fournisseurId = fournisseurJson.has("fournisseurId")
                ? fournisseurJson.get("fournisseurId").asLong()
                : fournisseurJson.get("id").asLong();

        MatierePremiereRequestDTO requestDTO = new MatierePremiereRequestDTO();
        requestDTO.setName("Acier");
        requestDTO.setStockQuantity(100);
        requestDTO.setStockMinimum(20);
        requestDTO.setUnit("kg");
        requestDTO.setFournisseurIds(Collections.singletonList(fournisseurId));

        mockMvc.perform(post("/api/v1/matieres-premieres")
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
        FournisseurRequestDTO fournisseurRequestDTO = new FournisseurRequestDTO();
        fournisseurRequestDTO.setName("Fournisseur1");
        fournisseurRequestDTO.setContactEmail("fr@hotmail.go");
        fournisseurRequestDTO.setPhoneNumber("+1234567890");
        fournisseurRequestDTO.setAddress("123 Rue Principale, Ville, Pays");
        fournisseurRequestDTO.setIsActive(true);
        fournisseurRequestDTO.setLeadTimeDays(7);
        fournisseurRequestDTO.setRating(4.5);

        MvcResult fournisseurResult = mockMvc.perform(post("/api/v1/fournisseurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fournisseurRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode fournisseurJson = objectMapper.readTree(fournisseurResult.getResponse().getContentAsString());
        long fournisseurId = fournisseurJson.has("fournisseurId")
                ? fournisseurJson.get("fournisseurId").asLong()
                : fournisseurJson.get("id").asLong();

        MatierePremiereRequestDTO requestDTO = new MatierePremiereRequestDTO();
        requestDTO.setName("Acier");
        requestDTO.setStockQuantity(100);
        requestDTO.setStockMinimum(20);
        requestDTO.setUnit("kg");
        requestDTO.setFournisseurIds(Collections.singletonList(fournisseurId));

        MvcResult matiereResult = mockMvc.perform(post("/api/v1/matieres-premieres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode matiereJson = objectMapper.readTree(matiereResult.getResponse().getContentAsString());
        JsonNode dataNode = matiereJson.get("data");
        long matiereId = dataNode.has("matierePremiereId")
                ? dataNode.get("matierePremiereId").asLong()
                : dataNode.get("id").asLong();
        requestDTO.setMatierePremiereId(matiereId);

        mockMvc.perform(get("/api/v1/matieres-premieres/" + matiereId)
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
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void testDeleteMatierePremiere() throws Exception {
        FournisseurRequestDTO fournisseurRequestDTO = new FournisseurRequestDTO();
        fournisseurRequestDTO.setName("Fournisseur1");
        fournisseurRequestDTO.setContactEmail("fr@hotmail.go");
        fournisseurRequestDTO.setPhoneNumber("+1234567890");
        fournisseurRequestDTO.setAddress("123 Rue Principale, Ville, Pays");
        fournisseurRequestDTO.setIsActive(true);
        fournisseurRequestDTO.setLeadTimeDays(7);
        fournisseurRequestDTO.setRating(4.5);

        MvcResult fournisseurResult = mockMvc.perform(post("/api/v1/fournisseurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fournisseurRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode fournisseurJson = objectMapper.readTree(fournisseurResult.getResponse().getContentAsString());
        long fournisseurId = fournisseurJson.has("fournisseurId")
                ? fournisseurJson.get("fournisseurId").asLong()
                : fournisseurJson.get("id").asLong();

        MatierePremiereRequestDTO requestDTO = new MatierePremiereRequestDTO();
        requestDTO.setName("Acier");
        requestDTO.setStockQuantity(100);
        requestDTO.setStockMinimum(20);
        requestDTO.setUnit("kg");
        requestDTO.setFournisseurIds(Collections.singletonList(fournisseurId));

        MvcResult matiereResult = mockMvc.perform(post("/api/v1/matieres-premieres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode matiereJson = objectMapper.readTree(matiereResult.getResponse().getContentAsString());
        JsonNode dataNode = matiereJson.get("data");
        long matiereId = dataNode.has("matierePremiereId")
                ? dataNode.get("matierePremiereId").asLong()
                : dataNode.get("id").asLong();

        mockMvc.perform(delete("/api/v1/matieres-premieres/" + matiereId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testUpdateMatierePremiere() throws Exception {
        FournisseurRequestDTO fournisseurRequestDTO = new FournisseurRequestDTO();
        fournisseurRequestDTO.setName("Fournisseur1");
        fournisseurRequestDTO.setContactEmail("fr@hotmail.go");
        fournisseurRequestDTO.setPhoneNumber("+1234567890");
        fournisseurRequestDTO.setAddress("123 Rue Principale, Ville, Pays");
        fournisseurRequestDTO.setIsActive(true);
        fournisseurRequestDTO.setLeadTimeDays(7);
        fournisseurRequestDTO.setRating(4.5);

        MvcResult fournisseurResult = mockMvc.perform(post("/api/v1/fournisseurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fournisseurRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode fournisseurJson = objectMapper.readTree(fournisseurResult.getResponse().getContentAsString());
        long fournisseurId = fournisseurJson.has("fournisseurId")
                ? fournisseurJson.get("fournisseurId").asLong()
                : fournisseurJson.get("id").asLong();

        MatierePremiereRequestDTO requestDTO = new MatierePremiereRequestDTO();
        requestDTO.setName("Acier");
        requestDTO.setStockQuantity(100);
        requestDTO.setStockMinimum(20);
        requestDTO.setUnit("kg");
        requestDTO.setFournisseurIds(Collections.singletonList(fournisseurId));

        MvcResult matiereResult = mockMvc.perform(post("/api/v1/matieres-premieres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode matiereJson = objectMapper.readTree(matiereResult.getResponse().getContentAsString());
        JsonNode dataNode = matiereJson.get("data");
        long matiereId = dataNode.has("matierePremiereId")
                ? dataNode.get("matierePremiereId").asLong()
                : dataNode.get("id").asLong();

        MatierePremiereRequestDTO updateDTO = new MatierePremiereRequestDTO();
        updateDTO.setName("Aluminium");
        updateDTO.setStockQuantity(150);
        updateDTO.setStockMinimum(30);
        updateDTO.setUnit("kg");
        updateDTO.setFournisseurIds(Collections.singletonList(fournisseurId));
        mockMvc.perform(put("/api/v1/matieres-premieres/" + matiereId)
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
        mockMvc.perform(get("/api/v1/matieres-premieres/filtrer-par-stock-critique")
                        .param("stockCritique", "50")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

}