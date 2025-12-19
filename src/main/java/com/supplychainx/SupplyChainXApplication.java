package com.supplychainx;

import com.supplychainx.service_user.model.Role;
import com.supplychainx.service_user.model.User;
import com.supplychainx.service_user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SupplyChainXApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyChainXApplication.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "Welcome to SupplyChainX Application!";
    }
//
//    @Bean
//    CommandLineRunner start(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        return args -> {
//            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
//                User user = new User();
//                user.setFirstName("Admin");
//                user.setLastName("Admin");
//                user.setEmail("admin@gmail.com");
//                user.setPassword(passwordEncoder.encode("admin123"));
//                user.setIsActive(true);
//                user.setIsDeleted(false);
//                Role role = new Role();
//                role.setRoleId(1L);
//                role.setName("ADMIN");
//                user.setRole(role);
//                userRepository.save(user);
//            }
//        };
//    }

}
