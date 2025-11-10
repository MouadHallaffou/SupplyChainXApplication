package com.supplychainx.service_livraison.dto.Response;

import com.supplychainx.service_livraison.model.enums.ClientOrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ClientOrderResponseDTO(
        Long orderId,
        String orderNumber,
        ClientResponseDTO client,
        AddressResponseDTO deliveryAddress,
        ClientOrderStatus status,
        Double totalAmount,
        List<OrderProductClientResponseDTO> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}