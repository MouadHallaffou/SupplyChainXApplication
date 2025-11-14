package com.supplychainx.integration.user_service.repository;

import com.supplychainx.service_user.model.Role;
import com.supplychainx.service_user.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(
        locations = "classpath:application-test.properties",
        properties = {
                "spring.config.location=classpath:application-test.properties",
                "spring.config.name=application-test"
        }
)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testSave(){
        Role role = new Role();
        role.setName("ADMIN");
        Role savedRole = roleRepository.save(role);

        assertThat(savedRole.getRoleId()).isNotNull();
        assertThat(savedRole.getName()).isEqualTo("ADMIN");
    }

    @Test
    void testExistsByName(){
        Role role = new Role();
        role.setName("USER");
        roleRepository.save(role);

        Boolean exists = roleRepository.existsRolesByName("USER");
        assertThat(exists).isTrue();
    }

    @Test
    void testFindById(){
        Role role = new Role();
        role.setName("MANAGER");
        Role savedRole = roleRepository.save(role);

        Role foundRole = roleRepository.findById(savedRole.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));
        assertThat(foundRole).isNotNull();
        assertThat(foundRole.getName()).isEqualTo("MANAGER");
    }

    @Test
    void testFindAll(){
        Role role1 = new Role();
        role1.setName("ADMIN");
        Role role2 = new Role();
        role2.setName("USER");
        roleRepository.save(role1);
        roleRepository.save(role2);

        var roles = roleRepository.findAll();
        assertThat(roles).hasSize(2);
    }

    @Test
    void testDelete(){
        Role role = new Role();
        role.setName("Admin");
        Role savedRole = roleRepository.save(role);

        roleRepository.deleteById(savedRole.getRoleId());
        Boolean exists = roleRepository.existsRolesByName("Admin");
        assertThat(exists).isFalse();
    }

}
