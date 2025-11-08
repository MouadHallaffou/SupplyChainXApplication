package com.supplychainx.service_production.dto.Response;

import java.time.LocalDateTime;

public record ProductResponseDTO(
        Long productId,
        String name,
        Integer productionTimeHours,
        Double costPerUnit,
        Integer stock,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
