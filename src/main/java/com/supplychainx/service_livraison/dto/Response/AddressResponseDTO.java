package com.supplychainx.service_livraison.dto.Response;

import java.time.LocalDateTime;

public record AddressResponseDTO(
        Long addressId,
        String street,
        String city,
        String state,
        String zipCode,
        String country,
        Long clientId,
        String clientName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
