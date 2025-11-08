package com.supplychainx.service_livraison.controller.graphql.resolver.mutation;

import com.supplychainx.service_livraison.dto.Request.ClientRequestDTO;
import com.supplychainx.service_livraison.dto.Response.ClientResponseDTO;
import com.supplychainx.service_livraison.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ClientMutation {

    private final ClientService clientService;

    @MutationMapping
    public ClientResponseDTO createClient(@Valid @Argument ClientRequestDTO input) {
        return clientService.createClient(input);
    }

    @MutationMapping
    public ClientResponseDTO updateClient(@Valid @Argument Long id, @Argument ClientRequestDTO input) {
        return clientService.updateClient(id, input);
    }

    @MutationMapping
    public Boolean deleteClient(@Argument Long id) {
        clientService.deleteClient(id);
        return true;
    }

}