package com.supplychainx.service_livraison.controller.graphql.resolver.query;

import com.supplychainx.service_livraison.dto.Response.ClientOrderResponseDTO;
import com.supplychainx.service_livraison.service.ClientOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ClientOrderQuery {

    private final ClientOrderService clientOrderService;

    @QueryMapping
    public Page<ClientOrderResponseDTO> getAllClientOrders(
            @Argument Integer page,
            @Argument Integer size,
            @Argument String sortBy) {

        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 10,
                Sort.by(sortBy != null ? sortBy : "createdAt").descending()
        );

        return clientOrderService.getAllClientOrders(pageable);
    }

    @QueryMapping
    public ClientOrderResponseDTO getClientOrderById(@Argument Long orderId) {
        return clientOrderService.getClientOrderById(orderId);
    }

    @QueryMapping
    public Page<ClientOrderResponseDTO> getClientOrdersByStatus(
            @Argument String status,
            @Argument Integer page,
            @Argument Integer size) {

        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 10
        );

        return clientOrderService.getClientOrdersByStatus(
                com.supplychainx.service_livraison.model.enums.ClientOrderStatus.valueOf(status),
                pageable
        );
    }

}
