package com.supplychainx.integration.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychainx.service_user.dto.Request.RoleRequestDTO;
import com.supplychainx.service_user.dto.Request.UserRequestDTO;
import com.supplychainx.service_user.dto.Response.RoleResponseDTO;
import com.supplychainx.service_user.dto.Response.UserResponseDTO;
import com.supplychainx.service_user.repository.UserRepository;
import com.supplychainx.service_user.service.RoleService;
import com.supplychainx.service_user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

public class UserControllerTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testFindById() throws Exception {
        RoleRequestDTO role = new RoleRequestDTO();
        role.setName("USER");
        RoleResponseDTO savedRole = roleService.create(role);

        UserRequestDTO user = new UserRequestDTO();
        user.setFirstName("Mouad");
        user.setLastName("Hallaffou");
        user.setEmail("mouad@gmail.com");
        user.setPassword("123456");
        user.setRoleId(savedRole.roleId());
        user.setIsActive(true);
        user.setIsDeleted(false);

        UserResponseDTO createdUser = userService.create(user);
        UserResponseDTO foundUser = userService.getById(createdUser.userId());
        mockMvc.perform(get("/api/v1/users/"+createdUser.userId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(foundUser.firstName()))
                .andExpect(jsonPath("$.email").value(foundUser.email()));
    }


}