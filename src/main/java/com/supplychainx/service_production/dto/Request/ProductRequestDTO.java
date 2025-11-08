package com.supplychainx.service_production.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequestDTO {
    private Long productId;
    @NotBlank(message = "Le nom du produit ne peut pas être vide")
    private String name;
    @NotNull(message = "Le temps de production ne peut pas être nul")
    private Integer productionTimeHours;
    @NotNull(message = "Le coût par unité ne peut pas être nul")
    private Double costPerUnit;
    @NotNull(message = "Le stock ne peut pas être nul")
    private Integer stock;
}
