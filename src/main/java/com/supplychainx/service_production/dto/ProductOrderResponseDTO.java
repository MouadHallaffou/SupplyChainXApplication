package com.supplychainx.service_production.dto;

import com.supplychainx.service_production.model.enums.ProductionOrderStatus;

import java.time.LocalDateTime;

public record ProductOrderResponseDTO(
        Long productOrderId,
        Integer quantity,
        ProductionOrderStatus status,
        Long productId,
        LocalDateTime startDate,
        LocalDateTime endDate) {
}
