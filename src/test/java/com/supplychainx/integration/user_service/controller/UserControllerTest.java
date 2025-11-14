package com.supplychainx.integration.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychainx.service_user.dto.Request.RoleRequestDTO;
import com.supplychainx.service_user.dto.Request.UserRequestDTO;
import com.supplychainx.service_user.dto.Response.RoleResponseDTO;
import com.supplychainx.service_user.dto.Response.UserResponseDTO;
import com.supplychainx.service_user.repository.RoleRepository;
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

public class UserControllerTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testCreateUser_Success() throws Exception {
        RoleRequestDTO role = new RoleRequestDTO();
        role.setName("ADMIN");
        RoleResponseDTO savedRole = roleService.create(role);

        UserRequestDTO user = new UserRequestDTO();
        user.setFirstName("Mouad");
        user.setLastName("Hallaffou");
        user.setEmail("mouad@gmail.com");
        user.setPassword("123456");
        user.setRoleId(savedRole.roleId());
        user.setIsActive(true);
        user.setIsDeleted(false);

        mockMvc.perform(post("/api/v1/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.firstName").value("Mouad"))
                .andExpect(jsonPath("$.data.email").value("mouad@gmail.com"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testFindById_Success() throws Exception {
        RoleRequestDTO role = new RoleRequestDTO();
        role.setName("ADMIN");
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
        mockMvc.perform(get("/api/v1/users/" + createdUser.userId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(foundUser.firstName()))
                .andExpect(jsonPath("$.email").value(foundUser.email()));
    }

    @Test
    void testFindById_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id: 999"))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testCreateUser_InvalidInput() throws Exception {
        UserRequestDTO user = new UserRequestDTO();
        user.setFirstName(""); // Invalid first name
        user.setLastName("Hallaffou");
        user.setEmail("invalid-email"); // Invalid email
        user.setPassword("123456");
        user.setRoleId(1L);
        user.setIsActive(true);
        user.setIsDeleted(false);

        mockMvc.perform(post("/api/v1/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testCreateUser_RoleNotFound() throws Exception {
        UserRequestDTO user = new UserRequestDTO();
        user.setFirstName("Mouad");
        user.setLastName("Hallaffou");
        user.setEmail("mouad@gmail.ma");
        user.setPassword("123456");
        user.setRoleId(999L); // Non-existent role ID
        user.setIsActive(true);
        user.setIsDeleted(false);
        mockMvc.perform(post("/api/v1/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Role not found with id: 999"))
                .andExpect(jsonPath("$.success").value(false));
    }


    @Test
    void testActivateUser_Success() throws Exception {
        RoleRequestDTO role = new RoleRequestDTO();
        role.setName("ADMIN");
        RoleResponseDTO savedRole = roleService.create(role);

        UserRequestDTO user = new UserRequestDTO();
        user.setFirstName("Mouad");
        user.setLastName("Hallaffou");
        user.setEmail("mouad@gmail.com");
        user.setPassword("123456");
        user.setRoleId(savedRole.roleId());
        user.setIsActive(false);
        user.setIsDeleted(false);
        UserResponseDTO createdUser = userService.create(user);
        mockMvc.perform(put("/api/v1/users/activate/" + createdUser.userId()))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User activated successfully"));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        RoleRequestDTO role = new RoleRequestDTO();
        role.setName("ADMIN");
        RoleResponseDTO savedRole = roleService.create(role);

        UserRequestDTO user = new UserRequestDTO();
        user.setFirstName("Mouad");
        user.setLastName("Hallaffou");
        user.setEmail("mouadup@mail.com");
        user.setPassword("123456");
        user.setRoleId(savedRole.roleId());
        user.setIsActive(true);
        user.setIsDeleted(false);
        UserResponseDTO createdUser = userService.create(user);
        user.setFirstName("UpdatedName");
        mockMvc.perform(put("/api/v1/users/" + createdUser.userId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("UpdatedName"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testDeactivateUser_Success() throws Exception {
        RoleRequestDTO role = new RoleRequestDTO();
        role.setName("ADMIN");
        RoleResponseDTO savedRole = roleService.create(role);

        UserRequestDTO user = new UserRequestDTO();
        user.setFirstName("Mouad");
        user.setLastName("Hallaffou");
        user.setEmail("mouad@gmai.ma");
        user.setPassword("123456");
        user.setRoleId(savedRole.roleId());
        user.setIsActive(true);
        user.setIsDeleted(false);
        UserResponseDTO createdUser = userService.create(user);
        mockMvc.perform(put("/api/v1/users/deactivate/" + createdUser.userId()))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User deactivated successfully"));
    }

    @Test
    void testSoftDeleteUser_Success() throws Exception {
        RoleRequestDTO role = new RoleRequestDTO();
        role.setName("ADMIN");
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
        mockMvc.perform(put("/api/v1/users/softDelete/" + createdUser.userId()))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

}