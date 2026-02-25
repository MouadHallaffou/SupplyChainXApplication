package com.supplychainx.integration.service_livraison.controller;

import com.supplychainx.service_livraison.model.Address;
import com.supplychainx.service_livraison.model.Client;
import com.supplychainx.service_livraison.repository.AddressRepository;
import com.supplychainx.service_livraison.repository.ClientRepository;
import com.supplychainx.service_user.model.Role;
import com.supplychainx.service_user.model.User;
import com.supplychainx.service_user.repository.RoleRepository;
import com.supplychainx.service_user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
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
class AddressGraphQLIntegrationTest {

    @Autowired
    private GraphQlTester graphQlTester;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    private Address testAddress;
    private Client testClient;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        clientRepository.deleteAll();
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

        testClient = clientRepository.save(createTestClient());
        testAddress = createTestAddress();
    }

    private Client createTestClient() {
        Client client = new Client();
        client.setName("Test Client");
        client.setEmail("cl@gmail.com");
        client.setPhoneNumber("123-456-7890");
        return client;
    }

    private Address createTestAddress() {
        Address address = new Address();
        address.setStreet("123 Test Street");
        address.setCity("Test City");
        address.setState("Test State");
        address.setCountry("Test Country");
        address.setZipCode("12345");
        address.setClient(testClient);
        return addressRepository.save(address);
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testCreateAddress() {
        String mutation = """
            mutation($input: AddressInput!) {
                createAddress(input: $input) {
                    street
                    city
                    state
                    country
                    zipCode
                }
            }
        """;

        Map<String, Object> input = Map.of(
                "street", "456 New Street",
                "city", "New City",
                "state", "New State",
                "country", "New Country",
                "zipCode", "67890",
                "clientId", testClient.getClientId()
        );

        graphQlTester.document(mutation)
                .variable("input", input)
                .execute()
                .path("createAddress.street").entity(String.class).isEqualTo("456 New Street")
                .path("createAddress.city").entity(String.class).isEqualTo("New City");

        assertThat(addressRepository.count()).isEqualTo(2);
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testUpdateAddress() {
        String mutation = """
            mutation($id: ID!, $input: AddressInput!) {
                updateAddress(id: $id, input: $input) {
                    addressId
                    street
                    city
                    zipCode
                }
            }
        """;

        Map<String, Object> input = Map.of(
                "street", "789 Updated Street",
                "city", "Updated City",
                "state", "Updated State",
                "country", "Updated Country",
                "zipCode", "99999",
                "clientId", testAddress.getClient().getClientId()
        );

        graphQlTester.document(mutation)
                .variable("id", testAddress.getAddressId())
                .variable("input", input)
                .execute()
                .path("updateAddress.street").entity(String.class).isEqualTo("789 Updated Street")
                .path("updateAddress.city").entity(String.class).isEqualTo("Updated City")
                .path("updateAddress.zipCode").entity(String.class).isEqualTo("99999");
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testGetAllAddresses() {
        String query = """
            query {
                getAllAddresses(page: 0, size: 10, sortBy: "addressId", sortDir: "asc") {
                    content {
                        addressId
                        street
                        city
                        state
                        country
                        zipCode
                        clientId
                    }
                    totalElements
                    totalPages
                }
            }
        """;

        graphQlTester.document(query)
                .execute()
                .path("getAllAddresses.content[0].street").entity(String.class).isEqualTo("123 Test Street")
                .path("getAllAddresses.content[0].city").entity(String.class).isEqualTo("Test City")
                .path("getAllAddresses.totalElements").entity(Integer.class).isEqualTo(1);
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testGetAddressById() {
        String query = """
            query($id: ID!) {
                getAddressById(id: $id) {
                    addressId
                    street
                    city
                    zipCode
                    clientId
                }
            }
        """;

        graphQlTester.document(query)
                .variable("id", testAddress.getAddressId())
                .execute()
                .path("getAddressById.street").entity(String.class).isEqualTo("123 Test Street")
                .path("getAddressById.city").entity(String.class).isEqualTo("Test City")
                .path("getAddressById.zipCode").entity(String.class).isEqualTo("12345");
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testDeleteAddress() {
        String mutation = """
            mutation($id: ID!) {
                deleteAddress(id: $id)
            }
        """;

        graphQlTester.document(mutation)
                .variable("id", testAddress.getAddressId())
                .executeAndVerify();

        assertThat(addressRepository.count()).isEqualTo(0);
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    void testGetAddressById_NotFound() {
        String query = """
            query($id: ID!) {
                getAddressById(id: $id) {
                    addressId
                }
            }
        """;

        graphQlTester.document(query)
                .variable("id", 999L)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors).isNotEmpty();
                });
    }
}