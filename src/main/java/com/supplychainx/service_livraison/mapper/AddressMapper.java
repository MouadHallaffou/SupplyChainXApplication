package com.supplychainx.service_livraison.mapper;

import com.supplychainx.service_livraison.dto.AddressRequestDTO;
import com.supplychainx.service_livraison.dto.AddressResponseDTO;
import com.supplychainx.service_livraison.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper (componentModel = "spring")
public interface AddressMapper {
    Address toEntity(AddressRequestDTO dto);

    AddressResponseDTO toResponseDTO(Address address);

    @Mapping(target = "addressId", ignore = true)
    void toUpdateEntity(AddressRequestDTO dto,@MappingTarget Address address);
}
