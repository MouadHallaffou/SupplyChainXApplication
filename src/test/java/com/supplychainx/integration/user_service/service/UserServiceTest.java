package com.supplychainx.integration.user_service.service;

import com.supplychainx.service_user.dto.Request.RoleRequestDTO;
import com.supplychainx.service_user.dto.Request.UserRequestDTO;
import com.supplychainx.service_user.dto.Response.RoleResponseDTO;
import com.supplychainx.service_user.dto.Response.UserResponseDTO;
import com.supplychainx.service_user.model.User;
import com.supplychainx.service_user.repository.RoleRepository;
import com.supplychainx.service_user.repository.UserRepository;
import com.supplychainx.service_user.service.RoleService;
import com.supplychainx.service_user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Transactional
@TestPropertySource(
        locations = "classpath:application-test.properties",
        properties = {
                "spring.config.location=classpath:application-test.properties",
                "spring.config.name=application-test"
        }
)
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    private RoleResponseDTO createRole() {
        RoleRequestDTO role = new RoleRequestDTO();
        role.setName("ADMIN");
        return roleService.create(role);
    }

    private UserRequestDTO createUser() {
        RoleResponseDTO role = createRole();
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setFirstName("mouad");
        userRequestDTO.setLastName("halaffou");
        userRequestDTO.setEmail("mouad@gmail.com");
        userRequestDTO.setPassword("123456");
        userRequestDTO.setRoleId(role.roleId());
        userRequestDTO.setIsActive(true);
        userRequestDTO.setIsDeleted(false);
        return userRequestDTO;
    }

    @Test
    void testCreateUser() {
        UserRequestDTO userRequestDTO = createUser();
        UserResponseDTO userResponseDTO = userService.create(userRequestDTO);
        assert userResponseDTO != null;
        assert userResponseDTO.userId() != null;
        assert userResponseDTO.firstName().equals("mouad");
        assert userResponseDTO.lastName().equals("halaffou");
        assert userResponseDTO.email().equals("mouad@gmail.com");
    }

    @Test
    void testFindUserById() {
        UserRequestDTO userRequestDTO = createUser();
        UserResponseDTO userResponseDTO = userService.create(userRequestDTO);

        UserResponseDTO findUser = userService.getById(userResponseDTO.userId());
        assert findUser != null;
        assert findUser.userId().equals(userResponseDTO.userId());
    }

    @Test
    void testDeleteUser() {
        UserRequestDTO userRequestDTO = createUser();
        UserResponseDTO userResponseDTO = userService.create(userRequestDTO);
        userService.deactivate(userResponseDTO.userId());
        UserResponseDTO deletedUser = userService.getById(userResponseDTO.userId());
        assert deletedUser != null;
        assert !deletedUser.isActive();
    }

    @Test
    void testUpdateUser() {
        UserRequestDTO userRequestDTO = createUser();
        UserResponseDTO userResponseDTO = userService.create(userRequestDTO);

        UserRequestDTO updateUserRequest = new UserRequestDTO();
        updateUserRequest.setFirstName("updatedName");
        updateUserRequest.setLastName("updatedLastName");
        updateUserRequest.setEmail("mouadhlf@gmail.com");
        updateUserRequest.setPassword("newpassword");
        updateUserRequest.setRoleId(userRequestDTO.getRoleId());
        updateUserRequest.setIsActive(true);
        updateUserRequest.setIsDeleted(false);
        UserResponseDTO updatedUser = userService.update(userResponseDTO.userId(), updateUserRequest);
        assert updatedUser != null;
        assert updatedUser.firstName().equals("updatedName");
        assert updatedUser.lastName().equals("updatedLastName");
    }

    @Test
    void testActivateUser() {
        UserRequestDTO userRequestDTO = createUser();
        UserResponseDTO userResponseDTO = userService.create(userRequestDTO);
        userService.deactivate(userResponseDTO.userId());
        userService.activate(userResponseDTO.userId());
        UserResponseDTO activatedUser = userService.getById(userResponseDTO.userId());
        assert activatedUser != null;
        assert activatedUser.isActive();
    }

    @Test
    void testGetUserByEmail() {
        UserRequestDTO userRequestDTO = createUser();
        UserResponseDTO userResponseDTO = userService.create(userRequestDTO);

        Optional<User> findUser = userService.findByEmail(userResponseDTO.email());
        assert findUser.isPresent();
        assert findUser.get().getUserId().equals(userResponseDTO.userId());
    }
    
    @Test
    void testFindAllUsers() {
        UserRequestDTO userRequestDTO1 = createUser();
        userService.create(userRequestDTO1);
        var users = userService.getAll();
        assert users.size() == 1;
    }

}
