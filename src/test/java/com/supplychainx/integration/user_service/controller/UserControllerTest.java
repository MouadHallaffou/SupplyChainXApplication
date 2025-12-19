package com.supplychainx.integration.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychainx.security.auth.dto.LoginRequestDto;
import com.supplychainx.service_user.dto.Request.RoleRequestDTO;
import com.supplychainx.service_user.dto.Request.UserRequestDTO;
import com.supplychainx.service_user.dto.Response.RoleResponseDTO;
import com.supplychainx.service_user.dto.Response.UserResponseDTO;
import com.supplychainx.service_user.model.Role;
import com.supplychainx.service_user.model.User;
import com.supplychainx.service_user.repository.RoleRepository;
import com.supplychainx.service_user.repository.UserRepository;
import com.supplychainx.service_user.service.RoleService;
import com.supplychainx.service_user.service.UserService;
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
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
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

    private RoleResponseDTO createRole(String name) {
        RoleRequestDTO role = new RoleRequestDTO();
        role.setName(name);
        return roleService.create(role);
    }

    private UserRequestDTO createUser(String firstName, String lastName, String email, String password, Long roleId, Boolean isActive, Boolean isDeleted) {
        UserRequestDTO user = new UserRequestDTO();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setRoleId(roleId);
        user.setIsActive(isActive);
        user.setIsDeleted(isDeleted);
        return user;
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

    @Test
    void testCreateUser_Success() throws Exception {
        RoleResponseDTO role = createRole("USER");
        UserRequestDTO user = createUser("Mouad", "Hallaffou", "mouad@gmail.com", "123456", role.roleId(), true, false);

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + loginAndGetToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.firstName").value("Mouad"))
                .andExpect(jsonPath("$.data.email").value("mouad@gmail.com"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testFindById_Success() throws Exception {
        RoleResponseDTO role = createRole("USER");
        UserRequestDTO user = createUser("Mouad", "Hallaffou", "mouad@gmail.com", "123456", role.roleId(), true, false);
        UserResponseDTO savedUser = userService.create(user);

        UserResponseDTO foundUser = userService.getById(savedUser.userId());
        mockMvc.perform(get("/api/v1/users/" + savedUser.userId())
                        .header("Authorization", "Bearer " + loginAndGetToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(foundUser.firstName()))
                .andExpect(jsonPath("$.email").value(foundUser.email()));
    }

    @Test
    void testFindById_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/users/999")
                        .header("Authorization", "Bearer " + loginAndGetToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id: 999"))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testCreateUser_InvalidInput() throws Exception {
        RoleResponseDTO role = createRole("USER");
        UserRequestDTO user = createUser("Mouad", "", "test-invalid", "123456", role.roleId(), true, false);

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + loginAndGetToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testCreateUser_RoleNotFound() throws Exception {
        RoleResponseDTO role = createRole("USER");
        UserRequestDTO user = createUser("Mouad", "Hallaffou", "mouad@gmail.com", "123456", 999L, true, false); // Invalid role ID

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + loginAndGetToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Role not found with id: 999"))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testActivateUser_Success() throws Exception {
        RoleResponseDTO role = createRole("USER");
        UserRequestDTO user = createUser("Mouad", "Hallaffou", "mouad@gmail.com", "123456", role.roleId(), true, false);
        UserResponseDTO createdUser = userService.create(user);

        mockMvc.perform(put("/api/v1/users/activate/" + createdUser.userId())
                .header("Authorization", "Bearer " + loginAndGetToken()))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User activated successfully"));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        RoleResponseDTO role = createRole("USER");
        UserRequestDTO user = createUser("Mouad", "Hallaffou", "mouad@gmail.com", "123456", role.roleId(), true, false);
        UserResponseDTO createdUser = userService.create(user);
        user.setFirstName("UpdatedName");
        mockMvc.perform(put("/api/v1/users/" + createdUser.userId())
                        .header("Authorization", "Bearer " + loginAndGetToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("UpdatedName"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testDeactivateUser_Success() throws Exception {
        RoleResponseDTO role = createRole("USER");
        UserRequestDTO user = createUser("Mouad", "Hallaffou", "mouad@gmail.com", "123456", role.roleId(), true, false);
        UserResponseDTO createdUser = userService.create(user);

        mockMvc.perform(put("/api/v1/users/deactivate/" + createdUser.userId())
                .header("Authorization", "Bearer " + loginAndGetToken()))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User deactivated successfully"));
    }

    @Test
    void testSoftDeleteUser_Success() throws Exception {
        RoleResponseDTO role = createRole("USER");
        UserRequestDTO user = createUser("Mouad", "Hallaffou", "mouad@gmail.com", "123456", role.roleId(), true, false);
        UserResponseDTO createdUser = userService.create(user);

        mockMvc.perform(put("/api/v1/users/softDelete/" + createdUser.userId())
                .header("Authorization", "Bearer " + loginAndGetToken()))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

}