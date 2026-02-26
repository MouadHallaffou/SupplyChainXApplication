package com.supplychainx;

import com.supplychainx.service_user.model.Role;
import com.supplychainx.service_user.model.User;
import com.supplychainx.service_user.repository.RoleRepository;
import com.supplychainx.service_user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@SpringBootApplication
@RestController
public class SupplyChainXApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyChainXApplication.class, args);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/")
    public String home() {
        log.info("Home endpoint accessed");
        return "Welcome to SupplyChainX Application!";
    }

    @Bean
    CommandLineRunner start(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        return args -> {
            Role adminRole = roleRepository.existsRolesByName("ADMIN") ?
                    roleRepository.findById(1L).orElse(null) : null;
            if (adminRole == null) {
                Role role = new Role();
                role.setName("ADMIN");
//                roleRepository.save(role);
            }

            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                User user = new User();
                user.setFirstName("Admin");
                user.setLastName("Admin");
                user.setEmail("admin@gmail.com");
                user.setPassword(passwordEncoder.encode("admin123"));
                user.setIsActive(true);
                user.setIsDeleted(false);
                Role role = new Role();
                role.setRoleId(1L);
                role.setName("ADMIN");
                user.setRole(role);
//                userRepository.save(user);
            }
        };
    }

}
