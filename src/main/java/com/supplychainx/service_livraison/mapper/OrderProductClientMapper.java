package com.supplychainx.service_livraison.mapper;

import com.supplychainx.service_livraison.dto.Request.OrderProductClientRequestDTO;
import com.supplychainx.service_livraison.dto.Response.OrderProductClientResponseDTO;
import com.supplychainx.service_livraison.model.OrderProductClient;
import org.springframework.stereotype.Component;

@Component
public class OrderProductClientMapper {

    public OrderProductClientResponseDTO toDTO(OrderProductClient entity) {
        return getOrderProductClientResponseDTO(entity);
    }

    static OrderProductClientResponseDTO getOrderProductClientResponseDTO(OrderProductClient entity) {
        if (entity == null) {
            return null;
        }

        return new OrderProductClientResponseDTO(
                entity.getId(),
                entity.getProduct() != null ? entity.getProduct().getProductId() : null,
                entity.getProduct() != null ? entity.getProduct().getName() : null,
                entity.getQuantity(),
                entity.getClientOrder() != null ? entity.getClientOrder().getClientOrderId() : null,
                entity.getClientOrder() != null ? entity.getClientOrder().getOrderNumber() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public OrderProductClient toEntity(OrderProductClientRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        OrderProductClient entity = new OrderProductClient();
        entity.setQuantity(dto.getQuantity());
        return entity;
    }
}