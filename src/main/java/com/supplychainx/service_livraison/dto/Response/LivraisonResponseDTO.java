package com.supplychainx.service_livraison.dto.Response;

import java.time.LocalDateTime;

public record LivraisonResponseDTO(
        Long livraisonId,
        Long clientOrderId,
        String vehicule,
        String driverName,
        String status,
        String address,
        Double cost,
        LocalDateTime deliveryDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}