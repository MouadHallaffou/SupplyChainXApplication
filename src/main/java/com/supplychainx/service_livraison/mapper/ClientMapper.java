package com.supplychainx.service_livraison.mapper;

import com.supplychainx.service_livraison.dto.Request.ClientRequestDTO;
import com.supplychainx.service_livraison.dto.Response.AddressResponseDTO;
import com.supplychainx.service_livraison.dto.Response.ClientOrderResponseDTO;
import com.supplychainx.service_livraison.dto.Response.ClientResponseDTO;
import com.supplychainx.service_livraison.model.Client;
import com.supplychainx.service_livraison.model.ClientOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClientMapper {

    private final AddressMapper addressMapper;

    public Client toEntity(ClientRequestDTO clientRequestDTO) {
        if (clientRequestDTO == null) {
            return null;
        }

        Client client = new Client();
        client.setName(clientRequestDTO.getName());
        client.setEmail(clientRequestDTO.getEmail());
        client.setPhoneNumber(clientRequestDTO.getPhoneNumber());
        return client;
    }

    public ClientResponseDTO toResponseDTO(Client client) {
        return toResponseDTO(client, true);
    }

    public ClientResponseDTO toResponseDTO(Client client, boolean includeOrders) {
        if (client == null) {
            return null;
        }

        // Mapper les adresses en utilisant votre AddressMapper existant
        List<AddressResponseDTO> addresses = client.getAddresses() != null ?
                client.getAddresses().stream()
                        .map(addressMapper::toResponseDTO)
                        .collect(Collectors.toList()) :
                Collections.emptyList();

        // Mapper les commandes de manière simplifiée
        List<ClientOrderResponseDTO> orders = (includeOrders && client.getClientOrders() != null) ?
                client.getClientOrders().stream()
                        .map(this::mapClientOrderToSimplifiedDTO)
                        .collect(Collectors.toList()) :
                Collections.emptyList();

        return new ClientResponseDTO(
                client.getClientId(),
                client.getName(),
                client.getEmail(),
                client.getPhoneNumber(),
                addresses,
                orders,
                client.getCreatedAt(),
                client.getUpdatedAt()
        );
    }

    public ClientResponseDTO toSimplifiedResponseDTO(Client client) {
        if (client == null) {
            return null;
        }

        return new ClientResponseDTO(
                client.getClientId(),
                client.getName(),
                client.getEmail(),
                client.getPhoneNumber(),
                Collections.emptyList(), // Pas d'adresses
                Collections.emptyList(), // Pas de commandes
                client.getCreatedAt(),
                client.getUpdatedAt()
        );
    }

    private ClientOrderResponseDTO mapClientOrderToSimplifiedDTO(ClientOrder order) {
        if (order == null) {
            return null;
        }

        return new ClientOrderResponseDTO(
                order.getClientOrderId(),
                order.getOrderNumber(),
                null, // Pas de client pour éviter la circularité
                null, // Pas d'adresse ici
                order.getStatus(),
                order.getTotalAmount(),
                Collections.emptyList(), // Pas d'items ici
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    public void toUpdateEntity(ClientRequestDTO clientRequestDTO, Client client) {
        if (clientRequestDTO == null || client == null) {
            return;
        }

        if (clientRequestDTO.getName() != null) {
            client.setName(clientRequestDTO.getName());
        }
        if (clientRequestDTO.getEmail() != null) {
            client.setEmail(clientRequestDTO.getEmail());
        }
        if (clientRequestDTO.getPhoneNumber() != null) {
            client.setPhoneNumber(clientRequestDTO.getPhoneNumber());
        }
    }
}