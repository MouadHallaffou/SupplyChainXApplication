package com.supplychainx.service_production.dto;

import java.time.LocalDateTime;

public record BillOfMaterialResponseDTO(
        Long bomId,
        Integer quantity,
        Long productId,
        Long matierePremiereId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
