package com.supplychainx.service_production.mapper;

import com.supplychainx.service_production.dto.BillOfMaterialRequestDTO;
import com.supplychainx.service_production.dto.BillOfMaterialResponseDTO;
import com.supplychainx.service_production.model.BillOfMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BillOfMaterialMapper {
    @Mapping(target = "product.productId", source = "productId")
    @Mapping(target = "matierePremiere.matierePremiereId", source = "matierePremiereId")
    BillOfMaterial toEntity(BillOfMaterialRequestDTO dto);

    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "matierePremiereId", source = "matierePremiere.matierePremiereId")
    BillOfMaterialResponseDTO toResponseDto(BillOfMaterial billOfMaterial);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "matierePremiere", ignore = true)
    @Mapping(target = "bomId", ignore = true)
    void updateEntityFromDto(BillOfMaterialRequestDTO dto, @MappingTarget BillOfMaterial entity);
}
