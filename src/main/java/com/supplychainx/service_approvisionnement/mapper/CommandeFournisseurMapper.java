package com.supplychainx.service_approvisionnement.mapper;

import com.supplychainx.service_approvisionnement.dto.CommandeFournisseurRequestDTO;
import com.supplychainx.service_approvisionnement.dto.CommandeFournisseurResponseDTO;
import com.supplychainx.service_approvisionnement.dto.FournisseurRequestDTO;
import com.supplychainx.service_approvisionnement.model.CommandeFournisseur;
import com.supplychainx.service_approvisionnement.model.Fournisseur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CommandeFournisseurMatiereMapper.class})
public interface CommandeFournisseurMapper {

    @Mapping(target = "fournisseur.fournisseurId", source = "fournisseurId")
    CommandeFournisseur toEntity(CommandeFournisseurRequestDTO dto);

    @Mapping(target = "orderFournisseurId", source = "orderFournisseurId")
    @Mapping(target = "fournisseurName", source = "fournisseur.name")
    @Mapping(target = "commandeFournisseurMatieres", source = "commandeFournisseurMatieres")
    CommandeFournisseurResponseDTO toResponseDTO(CommandeFournisseur entity);

    void updateEntityFromDTO(CommandeFournisseurRequestDTO commandeFournisseurRequestDTO,@MappingTarget CommandeFournisseur existingCommande);
}
