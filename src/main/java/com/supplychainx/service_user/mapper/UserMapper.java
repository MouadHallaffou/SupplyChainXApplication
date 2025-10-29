package com.supplychainx.service_user.mapper;

import com.supplychainx.service_user.dto.UserRequestDTO;
import com.supplychainx.service_user.dto.UserResponseDTO;
import com.supplychainx.service_user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // convertir requestDTO to Entity
    @Mapping(target = "role.roleId", source = "roleId")
    User toEntity(UserRequestDTO dto);

    // convertir Entity to responseDTO
    @Mapping(target = "roleName", source = "role.name")
    @Mapping(target = "isActive", source = "isActive")
    UserResponseDTO toResponseDTO(User user);

}
