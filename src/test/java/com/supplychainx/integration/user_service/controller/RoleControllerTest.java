package com.supplychainx.integration.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.supplychainx.service_user.dto.Request.RoleRequestDTO;
import com.supplychainx.service_user.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
                "spring.config.location=classpath:application-test.properties",
                "spring.config.name=application-test"
        }
)
public class RoleControllerTest {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testCreateRole_Success() throws Exception {
        RoleRequestDTO role = new RoleRequestDTO();
        role.setName("ADMIN");
        mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("ADMIN"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testGetRoleById_Success() throws Exception {
        RoleRequestDTO role = new RoleRequestDTO();
        role.setName("MANAGER");
        MvcResult result = mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        long roleId = ((Number) JsonPath.read(json, "$.data.roleId")).longValue();

        mockMvc.perform(get("/api/v1/roles/" + roleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("MANAGER"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testUpdateRole_Success() throws Exception {
        RoleRequestDTO role = new RoleRequestDTO();
        role.setName("USER");
        MvcResult result = mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        long roleId = ((Number) JsonPath.read(json, "$.data.roleId")).longValue();

        RoleRequestDTO updatedRole = new RoleRequestDTO();
        updatedRole.setName("SUPERUSER");

        mockMvc.perform(put("/api/v1/roles/" + roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRole)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("SUPERUSER"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testDeleteRole_Success() throws Exception {
        RoleRequestDTO role = new RoleRequestDTO();
        role.setName("TEMP_ROLE");

        MvcResult result = mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        long roleId = ((Number) JsonPath.read(json, "$.data.roleId")).longValue();

        mockMvc.perform(delete("/api/v1/roles/" + roleId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testGetAllRoles_Success() throws Exception {
        RoleRequestDTO role1 = new RoleRequestDTO();
        role1.setName("ROLE_ONE");
        RoleRequestDTO role2 = new RoleRequestDTO();
        role2.setName("ROLE_TWO");

        mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role2)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

}
