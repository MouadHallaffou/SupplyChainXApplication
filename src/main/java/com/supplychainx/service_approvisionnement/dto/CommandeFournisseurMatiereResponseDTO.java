package com.supplychainx.service_approvisionnement.dto;

public record CommandeFournisseurMatiereResponseDTO(Long id, Long matierePremiereId, String matierePremiereName,
                                                    Integer quantite) {
}
