package com.supplychainx.service_production.mapper;

import com.supplychainx.service_production.dto.ProductRequestDTO;
import com.supplychainx.service_production.dto.ProductResponseDTO;
import com.supplychainx.service_production.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductRequestDTO dto);

    ProductResponseDTO toResponseDto(Product product);

    @Mapping(target = "productId", ignore = true)
    void updateEntityFromDto(ProductRequestDTO productRequestDTO,@MappingTarget Product entity);
}
