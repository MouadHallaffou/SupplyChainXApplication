package com.supplychainx.service_livraison.dto.Request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LivraisonRequestDTO {
    @NotNull(message = "Client Order ID cannot be null")
    private Long clientOrderId;
    @NotBlank(message = "Vehicule is required")
    private String vehicule;
    @NotBlank(message = "Driver Name is required")
    private String driverName;
    private String status;
    @NotNull(message = "Cost cannot be null")
    private Double cost;
    @Future(message = "Delivery date must be in the future")
    private LocalDateTime deliveryDate;
}
