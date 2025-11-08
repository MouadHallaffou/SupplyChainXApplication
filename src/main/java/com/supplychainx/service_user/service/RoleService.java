package com.supplychainx.service_user.service;

import com.supplychainx.service_user.dto.Request.RoleRequestDTO;
import com.supplychainx.service_user.dto.Response.RoleResponseDTO;

import java.util.List;

public interface RoleService {
    RoleResponseDTO create(RoleRequestDTO roleRequestDTO);
    RoleResponseDTO getById(Long id);
    RoleResponseDTO update(Long id, RoleRequestDTO roleRequestDTO);
    void delete(Long id);
    List<RoleResponseDTO> getAll();
}