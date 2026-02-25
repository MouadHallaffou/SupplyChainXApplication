package com.supplychainx.service_livraison.controller.graphql.resolver.mutation;

import com.supplychainx.service_livraison.dto.Request.ClientRequestDTO;
import com.supplychainx.service_livraison.dto.Response.ClientResponseDTO;
import com.supplychainx.service_livraison.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ClientMutation {

    private final ClientService clientService;

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LIVRAISONS', 'RESPONSABLE_LOGISTIQUE', 'GESTIONNAIRE_COMMERCIAL')")
    public ClientResponseDTO createClient(@Valid @Argument ClientRequestDTO input) {
        return clientService.createClient(input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LIVRAISONS', 'RESPONSABLE_LOGISTIQUE', 'GESTIONNAIRE_COMMERCIAL')")
    public ClientResponseDTO updateClient(@Valid @Argument Long id, @Argument ClientRequestDTO input) {
        return clientService.updateClient(id, input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LIVRAISONS', 'RESPONSABLE_LOGISTIQUE', 'GESTIONNAIRE_COMMERCIAL')")
    public Boolean deleteClient(@Argument Long id) {
        clientService.deleteClient(id);
        return true;
    }

}