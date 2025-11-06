package com.supplychainx.service_approvisionnement.dto;

import java.util.List;

public record MatierePremiereResponseDTO(String name, Integer stockQuantity, Integer stockMinimum, String unit, List<FournisseurResponseDTO> fournisseurs) {
}
