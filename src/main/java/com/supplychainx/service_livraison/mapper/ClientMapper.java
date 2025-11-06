package com.supplychainx.service_livraison.mapper;

import com.supplychainx.service_livraison.dto.ClientRequestDTO;
import com.supplychainx.service_livraison.dto.ClientResponseDTO;
import com.supplychainx.service_livraison.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    Client toEntity(ClientRequestDTO clientRequestDTO);

    @Mapping(target = "orders", source = "clientOrders")
    @Mapping(target = "addresses", source = "addresses")
    ClientResponseDTO toResponseDTO(Client client);

    @Mapping(target = "clientId", ignore = true)
    void toUpdateEntity(ClientRequestDTO clientRequestDTO,@MappingTarget Client client);
}
