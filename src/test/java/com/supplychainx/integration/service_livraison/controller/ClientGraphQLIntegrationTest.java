package com.supplychainx.integration.service_livraison.controller;

import com.supplychainx.service_livraison.dto.Request.ClientRequestDTO;
import com.supplychainx.service_livraison.model.Client;
import com.supplychainx.service_livraison.repository.ClientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureHttpGraphQlTester
@ActiveProfiles("test")
class ClientGraphQLIntegrationTest {

    @Autowired
    private HttpGraphQlTester graphQlTester;

    @Autowired
    private ClientRepository clientRepository;

    private Client testClient;

    @BeforeEach
    void setUp() {
        clientRepository.deleteAll();

        testClient = new Client();
        testClient.setName("Test Client");
        testClient.setEmail("test@example.com");
        testClient.setPhoneNumber("+1234567890");
        testClient = clientRepository.save(testClient);
    }

    @AfterEach
    void tearDown() {
        clientRepository.deleteAll();
    }

    @Test
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

        ClientRequestDTO input = new ClientRequestDTO();
        input.setName("New Client");
        input.setEmail("new@example.com");
        input.setPhoneNumber("+9876543210");

        graphQlTester.document(mutation)
                .variable("input", input)
                .execute()
                .path("createClient.name").entity(String.class).isEqualTo("New Client")
                .path("createClient.email").entity(String.class).isEqualTo("new@example.com")
                .path("createClient.phoneNumber").entity(String.class).isEqualTo("+9876543210");

        assertThat(clientRepository.count()).isEqualTo(2);
    }

    @Test
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

        ClientRequestDTO input = new ClientRequestDTO();
        input.setName("Updated Client");
        input.setEmail("updated@example.com");
        input.setPhoneNumber("+1111111111");

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
