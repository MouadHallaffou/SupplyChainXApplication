package com.supplychainx.service_livraison.dto;

import com.supplychainx.service_livraison.model.enums.OrderClientStatus;
import lombok.Data;

@Data
public class ClientOrderRequestDTO {
    private Long orderId;
    private String orderNumber;
    private ClientResponseDTO client;
    private AddressResponseDTO deliveryAddress;
    private OrderClientStatus status;
}
