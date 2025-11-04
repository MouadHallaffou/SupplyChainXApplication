package com.supplychainx.service_production.dto;

import com.supplychainx.service_production.model.Product;
import com.supplychainx.service_production.model.enums.ProductionOrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductOrderRequestDTO {
    private Long productOrderId;
    @NotNull(message = "Quantity cannot be null")
    private Integer quantity;
    @NotNull(message = "Status cannot be null")
    private ProductionOrderStatus status;
    @NotNull(message = "Product ID cannot be null")
    private Long productId;
    @NotNull(message = "Start date cannot be null")
    private LocalDateTime startDate;
    @NotNull(message = "End date cannot be null")
    private LocalDateTime endDate;
}
