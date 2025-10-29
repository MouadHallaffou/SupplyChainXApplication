package com.supplychainx.service_user.mapper;

import com.supplychainx.service_user.model.Role;
import com.supplychainx.service_user.dto.RoleDTO;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {
    public RoleDTO toDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setRoleId(role.getRoleId());
        dto.setName(role.getName());
        return dto;
    }

    public Role toEntity(RoleDTO dto) {
        Role role = new Role();
        role.setRoleId(dto.getRoleId());
        role.setName(dto.getName());
        return role;
    }

    public void updateEntity(Role role, RoleDTO dto) {
        role.setName(dto.getName());
    }
}
