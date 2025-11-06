package com.supplychainx.service_livraison.dto;

import com.supplychainx.service_livraison.model.enums.OrderClientStatus;

import java.time.LocalDateTime;

public record ClientOrderResponseDTO(Long orderId,
                                     String orderNumber,
                                     ClientResponseDTO client,
                                     AddressResponseDTO deliveryAddress,
                                     OrderClientStatus status,
                                     LocalDateTime createdAt,
                                     LocalDateTime updatedAt) {
}
