package com.supplychainx.service_approvisionnement.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record MatierePremiereResponseDTO(String name, Integer stockQuantity, Integer stockMinimum, String unit, List<FournisseurResponseDTO> fournisseurs) {
}
