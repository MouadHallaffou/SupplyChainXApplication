package com.supplychainx.service_approvisionnement.mapper;

import com.supplychainx.service_approvisionnement.dto.CommandeFournisseurMatiereRequestDTO;
import com.supplychainx.service_approvisionnement.dto.CommandeFournisseurMatiereResponseDTO;
import com.supplychainx.service_approvisionnement.model.CommandeFournisseurMatiere;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommandeFournisseurMatiereMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "matierePremiereId", source = "matierePremiere.matierePremiereId")
    @Mapping(target = "matierePremiereName", source = "matierePremiere.name")
    @Mapping(target = "quantite", source = "quantite")
    CommandeFournisseurMatiereResponseDTO toResponseDTO(CommandeFournisseurMatiere entity);

    @Mapping(target = "commandeFournisseur", ignore = true)
    @Mapping(target = "matierePremiere.matierePremiereId", source = "matierePremiereId")
    @Mapping(target = "quantite", source = "quantite")
    CommandeFournisseurMatiere toEntity(CommandeFournisseurMatiereRequestDTO dto);
}


