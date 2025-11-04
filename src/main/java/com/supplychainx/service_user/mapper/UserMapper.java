package com.supplychainx.service_user.mapper;

import com.supplychainx.service_user.dto.UserRequestDTO;
import com.supplychainx.service_user.dto.UserResponseDTO;
import com.supplychainx.service_user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // convertir requestDTO to Entity
    @Mapping(target = "role.roleId", source = "roleId")
    User toEntity(UserRequestDTO dto);

    // convertir Entity to responseDTO
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "roleName", source = "role.name", defaultValue = "No Role Assigned")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "isDeleted", source = "isDeleted")
    UserResponseDTO toResponseDTO(User user);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "role", ignore = true)
    void updateEntityFromDTO(UserRequestDTO userRequestDTO,@MappingTarget User existingUser);
}
