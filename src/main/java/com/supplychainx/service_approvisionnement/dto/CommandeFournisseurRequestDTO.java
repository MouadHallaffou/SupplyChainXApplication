package com.supplychainx.service_approvisionnement.dto;

import com.supplychainx.service_approvisionnement.model.enums.FournisseurOrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CommandeFournisseurRequestDTO {
    @NotNull(message = "Le fournisseur est obligatoire")
    private Long fournisseurId;
    @NotNull(message = "La date de la commande est obligatoire")
    private LocalDate orderDate;
    @NotNull(message = "Le statut de la commande est obligatoire")
    private FournisseurOrderStatus status;
    @NotNull(message = "Les matières premières de la commande sont obligatoires")
    private List<CommandeFournisseurMatiereRequestDTO> commandeFournisseurMatieres;
}
