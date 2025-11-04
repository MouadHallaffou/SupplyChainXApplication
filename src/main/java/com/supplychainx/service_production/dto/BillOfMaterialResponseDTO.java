package com.supplychainx.service_production.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BillOfMaterialResponseDTO(
        Long bomId,
        Integer quantity,
        Long productId,
        Long matierePremiereId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
