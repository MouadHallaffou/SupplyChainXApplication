package com.supplychainx.service_production.dto;

import com.supplychainx.service_production.model.enums.ProductionOrderStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProductOrderResponseDTO(
        Long productOrderId,
        Integer quantity,
        ProductionOrderStatus status,
        Long productId,
        LocalDateTime startDate,
        LocalDateTime endDate) {
}
