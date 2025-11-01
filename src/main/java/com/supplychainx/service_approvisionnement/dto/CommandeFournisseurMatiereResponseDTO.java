package com.supplychainx.service_approvisionnement.dto;

import lombok.Builder;

@Builder
public record CommandeFournisseurMatiereResponseDTO(Long id, Long matierePremiereId, String matierePremiereName,
                                                    Integer quantite) {
}
