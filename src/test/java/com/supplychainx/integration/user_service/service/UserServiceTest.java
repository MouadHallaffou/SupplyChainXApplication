package com.supplychainx.integration.user_service.service;

import com.supplychainx.service_user.dto.Request.RoleRequestDTO;
import com.supplychainx.service_user.dto.Request.UserRequestDTO;
import com.supplychainx.service_user.dto.Response.RoleResponseDTO;
import com.supplychainx.service_user.dto.Response.UserResponseDTO;
import com.supplychainx.service_user.service.RoleService;
import com.supplychainx.service_user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

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

    @Test
    void testCreateUser() {
        RoleRequestDTO role = new RoleRequestDTO();
        role.setName("USER");
        // save role logic would be here if needed
        roleService.create(role);
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setFirstName("mouad");
        userRequestDTO.setLastName("halaffou");
        userRequestDTO.setEmail("mouad@gmail.com");
        userRequestDTO.setPassword("123456");
        userRequestDTO.setRoleId(1L);
        userRequestDTO.setIsActive(true);
        userRequestDTO.setIsDeleted(false);

        UserResponseDTO userResponseDTO = userService.create(userRequestDTO);
        assert userResponseDTO != null;
        assert userResponseDTO.userId() != null;
        assert userResponseDTO.firstName().equals("mouad");
        assert userResponseDTO.lastName().equals("halaffou");
        assert userResponseDTO.email().equals("mouad@gmail.com");
    }

    @Test
    void testFindUserById() {
        RoleRequestDTO role = new RoleRequestDTO();
        role.setName("USER");
        // save role logic would be here if needed
        roleService.create(role);
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setFirstName("mouad");
        userRequestDTO.setLastName("halaffou");
        userRequestDTO.setEmail("mouad@gmail.com");
        userRequestDTO.setPassword("123456");
        userRequestDTO.setRoleId(1L);
        userRequestDTO.setIsActive(true);
        userRequestDTO.setIsDeleted(false);

        UserResponseDTO userResponseDTO = userService.create(userRequestDTO);
        UserResponseDTO findUser = userService.getById(userResponseDTO.userId());
        assert findUser != null;
        assert findUser.userId().equals(1L);
    }

}
