package com.supplychainx.service_approvisionnement.dto;

import com.supplychainx.service_approvisionnement.model.enums.FournisseurOrderStatus;

import java.time.LocalDate;
import java.util.List;

public record CommandeFournisseurResponseDTO(Long orderFournisseurId, LocalDate orderDate,
                                             FournisseurOrderStatus status, String fournisseurName,
                                             List<CommandeFournisseurMatiereResponseDTO> commandeFournisseurMatieres) {
}
