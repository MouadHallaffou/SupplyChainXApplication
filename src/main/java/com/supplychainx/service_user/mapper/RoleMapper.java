package com.supplychainx.service_user.mapper;

import com.supplychainx.service_user.dto.RoleRequestDTO;
import com.supplychainx.service_user.dto.RoleResponseDTO;
import com.supplychainx.service_user.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toEntity(RoleRequestDTO dto);

    RoleResponseDTO toDTO(Role role);

    void updateEntityFromDTO(RoleRequestDTO roleRequestDTO,@MappingTarget Role existingRole);
}
