package com.supplychainx.service_approvisionnement.mapper;

import com.supplychainx.service_approvisionnement.dto.Request.FournisseurRequestDTO;
import com.supplychainx.service_approvisionnement.dto.Response.FournisseurResponseDTO;
import com.supplychainx.service_approvisionnement.model.Fournisseur;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FournisseurMapper {
    Fournisseur toEntity(FournisseurRequestDTO dto);

    FournisseurResponseDTO toResponseDTO(Fournisseur fournisseur);

    void updateEntityFromDTO(FournisseurRequestDTO dto, @MappingTarget Fournisseur entity);
}
