package com.supplychainx.service_approvisionnement.dto.Response;

import lombok.Builder;

@Builder
public record FournisseurResponseDTO(
        Long fournisseurId,
        String name,
        String contactEmail,
        String phoneNumber,
        String address,
        Double rating,
        Integer leadTimeDays,
        Boolean isActive) {
}
