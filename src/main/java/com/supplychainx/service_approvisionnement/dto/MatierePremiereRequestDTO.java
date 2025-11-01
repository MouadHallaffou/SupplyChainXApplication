package com.supplychainx.service_approvisionnement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MatierePremiereRequestDTO {
    private Long matierePremiereId;
    @NotBlank(message = "Le nom de la matière première est obligatoire")
    private String name;
    @NotNull(message = "La quantité en stock est obligatoire")
    private Integer stockQuantity;
    @NotNull(message = "Le stock minimum est obligatoire")
    private Integer stockMinimum;
    @NotBlank(message = "L'unité de mesure est obligatoire")
    private String unit;
    @NotEmpty(message = "Au moins un fournisseur est obligatoire")
    private List<Long> fournisseurIds = new ArrayList<>();
}
