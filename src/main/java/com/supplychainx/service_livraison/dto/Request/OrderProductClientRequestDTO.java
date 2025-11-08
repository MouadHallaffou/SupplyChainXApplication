package com.supplychainx.service_livraison.dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderProductClientRequestDTO {
    @NotNull(message = "Product ID is required")
    private Long productId;
    @NotNull(message = "Quantity is required")
    private Integer quantity;
}