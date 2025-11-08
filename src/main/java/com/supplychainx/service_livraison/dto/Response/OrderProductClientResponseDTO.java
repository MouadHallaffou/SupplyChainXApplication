package com.supplychainx.service_livraison.dto.Response;

import java.time.LocalDateTime;

public record OrderProductClientResponseDTO(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        Long clientOrderId,
        String orderNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
