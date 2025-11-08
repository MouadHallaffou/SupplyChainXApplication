package com.supplychainx.service_approvisionnement.dto.Response;

public record CommandeFournisseurMatiereResponseDTO(
        Long id,
        Long matierePremiereId,
        String matierePremiereName,
        Integer quantite) {
}
