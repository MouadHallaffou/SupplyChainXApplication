package com.supplychainx.service_production.dto.Response;

import java.time.LocalDateTime;

public record BillOfMaterialResponseDTO(
        Long bomId,
        Integer quantity,
        Long productId,
        Long matierePremiereId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
