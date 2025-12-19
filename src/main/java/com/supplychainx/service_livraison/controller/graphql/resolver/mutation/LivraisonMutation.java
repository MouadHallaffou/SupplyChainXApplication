package com.supplychainx.service_livraison.controller.graphql.resolver.mutation;

import com.supplychainx.service_livraison.dto.Request.LivraisonRequestDTO;
import com.supplychainx.service_livraison.dto.Response.LivraisonResponseDTO;
import com.supplychainx.service_livraison.service.LivraisonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class LivraisonMutation {

    private final LivraisonService livraisonService;

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LIVRAISONS', 'RESPONSABLE_LOGISTIQUE', 'GESTIONNAIRE_COMMERCIAL')")
    public LivraisonResponseDTO createLivraison(@Valid @Argument LivraisonRequestDTO input) {
        return livraisonService.createLivraison(input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LIVRAISONS', 'RESPONSABLE_LOGISTIQUE', 'GESTIONNAIRE_COMMERCIAL')")
    public LivraisonResponseDTO updateLivraison(@Argument Long id, @Valid @Argument LivraisonRequestDTO input) {
        return livraisonService.updateLivraison(id, input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LIVRAISONS', 'RESPONSABLE_LOGISTIQUE', 'GESTIONNAIRE_COMMERCIAL')")
    public LivraisonResponseDTO updateLivraisonStatus(@Argument Long id, @Argument String status) {
        return livraisonService.updateLivraisonStatus(id, status);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISEUR_LIVRAISONS', 'RESPONSABLE_LOGISTIQUE', 'GESTIONNAIRE_COMMERCIAL')")
    public void deleteLivraison(@Argument Long id) {
        livraisonService.deleteLivraison(id);
    }

}
