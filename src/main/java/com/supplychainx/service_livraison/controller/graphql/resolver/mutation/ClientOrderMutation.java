package com.supplychainx.service_livraison.controller.graphql.resolver.mutation;

import com.supplychainx.service_livraison.dto.Request.ClientOrderRequestDTO;
import com.supplychainx.service_livraison.dto.Response.ClientOrderResponseDTO;
import com.supplychainx.service_livraison.service.ClientOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ClientOrderMutation {

    private final ClientOrderService clientOrderService;

    @MutationMapping
    public ClientOrderResponseDTO createClientOrder(@Argument("input") ClientOrderRequestDTO input) {
        return clientOrderService.createClientOrder(input);
    }

    @MutationMapping
    public ClientOrderResponseDTO updateClientOrder(@Argument Long orderId, @Argument("input") ClientOrderRequestDTO input) {
        return clientOrderService.updateClientOrder(orderId, input);
    }

    @MutationMapping
    public ClientOrderResponseDTO cancelClientOrder(@Argument Long orderId) {
        return clientOrderService.cancelClientOrder(orderId);
    }

}