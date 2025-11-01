package com.supplychainx.service_user.controller;

import com.supplychainx.service_user.dto.RoleDTO;
import com.supplychainx.service_user.mapper.RoleMapper;
import com.supplychainx.service_user.model.Role;
import com.supplychainx.service_user.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @GetMapping
    public List<RoleDTO> getAllRoles() {
        return roleService.getAll()
                .stream()
                .map(roleMapper::toDTO)
                .toList();
    }

    @PostMapping
    public RoleDTO createRole(@Valid @RequestBody RoleDTO roleDTO) {
        Role role = roleMapper.toEntity(roleDTO);
        Role created = roleService.create(role);
        return roleMapper.toDTO(created);
    }

    @GetMapping("/{id}")
    public RoleDTO getRoleById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        return roleMapper.toDTO(role);
    }

    @PutMapping("/{id}")
    public RoleDTO updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO roleDTO) {
        Role role = roleService.getById(id);
        roleMapper.updateEntity(role, roleDTO);
        Role updated = roleService.update(role);
        return roleMapper.toDTO(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
