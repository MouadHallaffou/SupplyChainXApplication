package com.supplychainx.service_livraison.dto.Response;

import java.time.LocalDateTime;
import java.util.List;

public record ClientResponseDTO(
        Long clientId,
        String name,
        String email,
        String phoneNumber,
        List<AddressResponseDTO> addresses,
        List<ClientOrderResponseDTO> orders,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
