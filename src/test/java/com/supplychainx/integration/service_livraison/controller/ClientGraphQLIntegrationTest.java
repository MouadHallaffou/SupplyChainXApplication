package com.supplychainx.integration.service_livraison.controller;

import com.supplychainx.service_livraison.model.Client;
import com.supplychainx.service_livraison.repository.ClientRepository;
import com.supplychainx.service_user.model.Role;
import com.supplychainx.service_user.model.User;
import com.supplychainx.service_user.repository.RoleRepository;
import com.supplychainx.service_user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.ExecutionGraphQlServiceTester;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureGraphQlTester
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
class ClientGraphQLIntegrationTest {

    @Autowired
    private ClientRepository clientRepository;

    private Client testClient;

    @Autowired
    private ExecutionGraphQlServiceTester graphQlTester;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        clientRepository.deleteAll();
        testClient = new Client();
        testClient.setName("Test Client");
        testClient.setEmail("test@example.com");
        testClient.setPhoneNumber("+1234567890");
        testClient = clientRepository.save(testClient);
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

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testGetAllClients() {
        String query = """
                    query {
                        getAllClients(page: 0, size: 10, sortBy: "clientId", sortDir: "asc") {
                            content {
                                clientId
                                name
                                email
                                phoneNumber
                            }
                            totalElements
                            totalPages
                        }
                    }
                """;

        graphQlTester.document(query)
                .execute()
                .path("getAllClients.content[0].name").entity(String.class).isEqualTo("Test Client")
                .path("getAllClients.content[0].email").entity(String.class).isEqualTo("test@example.com")
                .path("getAllClients.totalElements").entity(Integer.class).isEqualTo(1);
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testGetClientById() {
        String query = """
                    query($id: ID!) {
                        getClientById(id: $id) {
                            clientId
                            name
                            email
                            phoneNumber
                        }
                    }
                """;

        graphQlTester.document(query)
                .variable("id", testClient.getClientId())
                .execute()
                .path("getClientById.name").entity(String.class).isEqualTo("Test Client")
                .path("getClientById.email").entity(String.class).isEqualTo("test@example.com");
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testCreateClient() {
        String mutation = """
                    mutation($input: ClientInput!) {
                        createClient(input: $input) {
                            clientId
                            name
                            email
                            phoneNumber
                        }
                    }
                """;

        Map<String, Object> input = Map.of(
                "name", "New Client",
                "email", "new@example.com",
                "phoneNumber", "+9876543210"
        );

        graphQlTester.document(mutation)
                .variable("input", input)
                .execute()
                .path("createClient.name").entity(String.class).isEqualTo("New Client")
                .path("createClient.email").entity(String.class).isEqualTo("new@example.com")
                .path("createClient.phoneNumber").entity(String.class).isEqualTo("+9876543210");

        assertThat(clientRepository.count()).isEqualTo(2);
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testUpdateClient() {
        String mutation = """
                    mutation($id: ID!, $input: ClientInput!) {
                        updateClient(id: $id, input: $input) {
                            clientId
                            name
                            email
                            phoneNumber
                        }
                    }
                """;

        Map<String, Object> input = Map.of(
                "name", "Updated Client",
                "email", "updated@example.com",
                "phoneNumber", "+1111111111"
        );

        graphQlTester.document(mutation)
                .variable("id", testClient.getClientId())
                .variable("input", input)
                .execute()
                .path("updateClient.name").entity(String.class).isEqualTo("Updated Client")
                .path("updateClient.email").entity(String.class).isEqualTo("updated@example.com");

        Client updated = clientRepository.findById(testClient.getClientId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated Client");
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testDeleteClient() {
        String mutation = """
                    mutation($id: ID!) {
                        deleteClient(id: $id)
                    }
                """;

        graphQlTester.document(mutation)
                .variable("id", testClient.getClientId())
                .execute()
                .path("deleteClient").entity(Boolean.class).isEqualTo(true);

        assertThat(clientRepository.count()).isEqualTo(0);
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testGetClientById_NotFound() {
        String query = """
                    query($id: ID!) {
                        getClientById(id: $id) {
                            clientId
                            name
                        }
                    }
                """;

        graphQlTester.document(query)
                .variable("id", 99999L)
                .execute()
                .errors()
                .expect(error -> error.getMessage().contains("Client not found"));
    }

}
