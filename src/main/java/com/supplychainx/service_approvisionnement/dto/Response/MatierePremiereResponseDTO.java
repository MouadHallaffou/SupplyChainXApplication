package com.supplychainx.service_approvisionnement.dto.Response;

import java.util.List;

public record MatierePremiereResponseDTO(
        Long matierePremiereId,
        String name,
        Integer stockQuantity,
        Integer stockMinimum,
        String unit, List<FournisseurResponseDTO> fournisseurs) {
}
