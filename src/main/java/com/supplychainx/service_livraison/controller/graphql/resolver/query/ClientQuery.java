package com.supplychainx.service_livraison.controller.graphql.resolver.query;

import com.supplychainx.service_livraison.dto.Response.ClientResponseDTO;
import com.supplychainx.service_livraison.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ClientQuery {

    private final ClientService clientService;

    @QueryMapping
    public Page<ClientResponseDTO> getAllClients(
            @Argument Integer page,
            @Argument Integer size,
            @Argument String sortBy,
            @Argument String sortDir) {
        return clientService.getAllClients(page, size, sortBy, sortDir);
    }

    @QueryMapping
    public ClientResponseDTO getClientById(@Argument Long id) {
        return clientService.getClientById(id);
    }

}