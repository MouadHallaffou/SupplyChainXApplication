package com.supplychainx.service_production.mapper;

import com.supplychainx.service_production.dto.Request.ProductOrderRequestDTO;
import com.supplychainx.service_production.dto.Response.ProductOrderResponseDTO;
import com.supplychainx.service_production.model.ProductOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductOrderMapper {
    @Mapping(target = "product.productId", source = "productId")
    ProductOrder toEntity(ProductOrderRequestDTO dto);

    @Mapping(target = "productId", source = "product.productId")
    ProductOrderResponseDTO toResponseDTO(ProductOrder entity);

    @Mapping(target = "product.productId" , source = "productId")
    void updateEntityFromDto(ProductOrderRequestDTO dto, @org.mapstruct.MappingTarget ProductOrder entity);
}
