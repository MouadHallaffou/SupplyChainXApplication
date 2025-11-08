package com.supplychainx.service_production.dto.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BillOfMaterialRequestDTO {
    private Long bomId;
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    @NotNull(message = "Product ID cannot be null")
    private Long productId;
    @NotNull(message = "Matiere Premiere ID cannot be null")
    private Long matierePremiereId;
}
