package com.supplychainx.service_livraison.dto.Request;

import com.supplychainx.service_livraison.model.enums.ClientOrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ClientOrderRequestDTO {
    @NotNull(message = "Client ID is required")
    private Long clientId;
    private ClientOrderStatus status;
    private Long deliveryAddressId;
    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderProductClientRequestDTO> items;
}
