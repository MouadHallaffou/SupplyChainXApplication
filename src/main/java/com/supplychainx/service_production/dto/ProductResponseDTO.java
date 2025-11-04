package com.supplychainx.service_production.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProductResponseDTO(
        Long productId, String name,
        Integer productionTimeHours,
        Double costPerUnit,
        Integer stock,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
