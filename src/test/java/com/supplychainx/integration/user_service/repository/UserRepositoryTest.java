package com.supplychainx.integration.user_service.repository;

import com.supplychainx.service_user.model.Role;
import com.supplychainx.service_user.model.User;
import com.supplychainx.service_user.repository.RoleRepository;
import com.supplychainx.service_user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(
        locations = "classpath:application-test.properties",
        properties = {
                "spring.config.location=classpath:application-test.properties",
                "spring.config.name=application-test"
        }
)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    private Role createRole(){
        Role role = new Role();
        role.setName("USER");
        roleRepository.save(role);
        return role;
    }

    private User createUser(){
        User user = new User();
        user.setFirstName("Mouad");
        user.setLastName("Hallaffou");
        user.setEmail("mouad@gmail.com");
        user.setPassword("123456");
        user.setIsActive(true);
        user.setIsDeleted(false);
        user.setRole(createRole());
        userRepository.save(user);
        return user;
    }

    @Test
    void testSave() {
        User user = createUser();
        User savedUser = userRepository.save(user);
        assertThat(savedUser.getUserId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("mouad@gmail.com");
    }

    @Test
    void testFindByEmail() {
        User user = createUser();
        User savedUser = userRepository.save(user);
        User foundUser = userRepository.findByEmail("mouad@gmail.com").orElseThrow(
                () -> new RuntimeException("User not found"));
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUserId()).isEqualTo(savedUser.getUserId());
    }

    @Test
    void testFindById() {
        User user = createUser();
        User savedUser = userRepository.save(user);
        User foundUser = userRepository.findById(savedUser.getUserId()).orElseThrow(
                () -> new RuntimeException("User not found"));
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("mouad@gmail.com");
    }

    @Test
    void testDeleteUser() {
        User user = createUser();
        User savedUser = userRepository.save(user);
        userRepository.delete(savedUser);
        boolean exists = userRepository.findById(savedUser.getUserId()).isPresent();
        assertThat(exists).isFalse();
    }

    @Test
    void testGetAllUsers() {
        User user = createUser();
        User savedUser = userRepository.save(user);
        List<User> users = userRepository.findAll();
        assertThat(users).isNotEmpty();
        assertThat(users).extracting("email").contains("mouad@gmail.com");
        assertThat(users).extracting("userId").contains(savedUser.getUserId());
    }

}
