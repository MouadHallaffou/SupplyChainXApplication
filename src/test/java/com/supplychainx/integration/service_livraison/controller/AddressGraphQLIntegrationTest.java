package com.supplychainx.integration.service_livraison.controller.graphql;

import com.supplychainx.service_livraison.dto.Request.AddressRequestDTO;
import com.supplychainx.service_livraison.model.Address;
import com.supplychainx.service_livraison.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureGraphQlTester
@Transactional
@ActiveProfiles("test")
class AddressGraphQLIntegrationTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private AddressRepository addressRepository;

    private Address testAddress;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        testAddress = createTestAddress();
    }

    private Address createTestAddress() {
        Address address = new Address();
        address.setStreet("123 Test Street");
        address.setCity("Test City");
        address.setState("Test State");
        address.setCountry("Test Country");
        address.setZipCode("12345");
        address.setClientId(1L);
        return addressRepository.save(address);
    }

    @Test
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
    void testCreateAddress() {
        String mutation = """
            mutation($input: AddressInput!) {
                createAddress(input: $input) {
                    addressId
                    street
                    city
                    state
                    country
                    zipCode
                    clientId
                }
            }
        """;

        AddressRequestDTO input = new AddressRequestDTO();
        input.setStreet("456 New Street");
        input.setCity("New City");
        input.setState("New State");
        input.setCountry("New Country");
        input.setZipCode("67890");
        input.setClientId(2L);

        graphQlTester.document(mutation)
                .variable("input", input)
                .execute()
                .path("createAddress.street").entity(String.class).isEqualTo("456 New Street")
                .path("createAddress.city").entity(String.class).isEqualTo("New City")
                .path("createAddress.zipCode").entity(String.class).isEqualTo("67890")
                .path("createAddress.clientId").entity(Long.class).isEqualTo(2L);

        assertThat(addressRepository.count()).isEqualTo(2);
    }

    @Test
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

        AddressRequestDTO input = new AddressRequestDTO();
        input.setStreet("789 Updated Street");
        input.setCity("Updated City");
        input.setState("Updated State");
        input.setCountry("Updated Country");
        input.setZipCode("99999");
        input.setClientId(testAddress.getClientId());

        graphQlTester.document(mutation)
                .variable("id", testAddress.getAddressId())
                .variable("input", input)
                .execute()
                .path("updateAddress.street").entity(String.class).isEqualTo("789 Updated Street")
                .path("updateAddress.city").entity(String.class).isEqualTo("Updated City")
                .path("updateAddress.zipCode").entity(String.class).isEqualTo("99999");
    }

    @Test
    void testDeleteAddress() {
        String mutation = """
            mutation($id: ID!) {
                deleteAddress(id: $id)
            }
        """;

        graphQlTester.document(mutation)
                .variable("id", testAddress.getAddressId())
                .execute()
                .path("deleteAddress").entity(Boolean.class).isEqualTo(true);

        assertThat(addressRepository.count()).isEqualTo(0);
    }

    @Test
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
