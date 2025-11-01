package com.supplychainx.service_approvisionnement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommandeFournisseurMatiereRequestDTO {
    @NotNull(message = "La matière première est obligatoire")
    private Long matierePremiereId;
    @NotNull(message = "La quantité est obligatoire")
    private Integer quantite;
}

