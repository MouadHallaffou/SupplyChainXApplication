package com.supplychainx.service_livraison.controller.graphql.resolver.query;

import com.supplychainx.service_livraison.dto.Response.LivraisonResponseDTO;
import com.supplychainx.service_livraison.service.LivraisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class LivraisonQuery {

    private final LivraisonService livraisonService;

    @QueryMapping
    public LivraisonResponseDTO getLivraisonById(@Argument Long id) {
        return livraisonService.getLivraisonById(id);
    }

    @QueryMapping
    public Page<LivraisonResponseDTO> getAllLivraisons(
            @Argument Integer page,
            @Argument Integer size,
            @Argument String sortBy,
            @Argument String sortDir) {
        return livraisonService.getAllLivraisons(page, size, sortBy, sortDir);
    }
}
