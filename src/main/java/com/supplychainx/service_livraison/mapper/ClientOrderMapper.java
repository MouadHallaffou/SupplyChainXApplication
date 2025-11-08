package com.supplychainx.service_livraison.mapper;

import com.supplychainx.service_livraison.dto.Request.ClientOrderRequestDTO;
import com.supplychainx.service_livraison.dto.Response.ClientOrderResponseDTO;
import com.supplychainx.service_livraison.dto.Response.ClientResponseDTO;
import com.supplychainx.service_livraison.dto.Response.OrderProductClientResponseDTO;
import com.supplychainx.service_livraison.model.ClientOrder;
import com.supplychainx.service_livraison.model.OrderProductClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.supplychainx.service_livraison.mapper.OrderProductClientMapper.getOrderProductClientResponseDTO;

@Component
@RequiredArgsConstructor
public class ClientOrderMapper {

    private final AddressMapper addressMapper;

    public ClientOrderResponseDTO toResponseDTO(ClientOrder entity) {
        if (entity == null) {
            return null;
        }

        List<OrderProductClientResponseDTO> items = entity.getOrderProducts() != null ?
                entity.getOrderProducts().stream()
                        .map(this::mapOrderProductToDTO)
                        .collect(Collectors.toList()) :
                Collections.emptyList();

        // Mapper le client de manière simplifiée
        ClientResponseDTO clientDTO = entity.getClient() != null ?
                new ClientResponseDTO(
                        entity.getClient().getClientId(),
                        entity.getClient().getName(),
                        entity.getClient().getEmail(),
                        entity.getClient().getPhoneNumber(),
                        Collections.emptyList(), // Pas d'adresses
                        Collections.emptyList(), // Pas de commandes
                        entity.getClient().getCreatedAt(),
                        entity.getClient().getUpdatedAt()
                ) : null;

        return new ClientOrderResponseDTO(
                entity.getClientOrderId(),
                entity.getOrderNumber(),
                clientDTO,
                // Utiliser votre AddressMapper existant
                entity.getDeliveryAddress() != null ?
                        addressMapper.toResponseDTO(entity.getDeliveryAddress()) : null,
                entity.getStatus(),
                entity.getTotalAmount(),
                items,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private OrderProductClientResponseDTO mapOrderProductToDTO(OrderProductClient orderProduct) {
        return getOrderProductClientResponseDTO(orderProduct);
    }

    public ClientOrder toEntity(ClientOrderRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return new ClientOrder();
    }
}