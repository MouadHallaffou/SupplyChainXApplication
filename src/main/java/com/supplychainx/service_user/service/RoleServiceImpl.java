package com.supplychainx.service_user.service;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_user.dto.RoleRequestDTO;
import com.supplychainx.service_user.dto.RoleResponseDTO;
import com.supplychainx.service_user.mapper.RoleMapper;
import com.supplychainx.service_user.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.supplychainx.service_user.repository.RoleRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    public RoleResponseDTO create(RoleRequestDTO roleRequestDTO) {
        Role role = roleMapper.toEntity(roleRequestDTO);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toDTO(savedRole);
    }

    @Override
    public RoleResponseDTO getById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return roleMapper.toDTO(role);
    }

    @Override
    @Transactional
    public RoleResponseDTO update(Long id, RoleRequestDTO roleRequestDTO) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        roleMapper.updateEntityFromDTO(roleRequestDTO, existingRole);
        Role updatedRole = roleRepository.save(existingRole);
        return roleMapper.toDTO(updatedRole);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (roleRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }

    @Override
    public List<RoleResponseDTO> getAll() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(roleMapper::toDTO)
                .toList();
    }
}