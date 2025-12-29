package com.supplychainx.service_user.controller;

import com.supplychainx.handler.GlobalSuccessHandler;
import com.supplychainx.service_user.dto.Request.RoleRequestDTO;
import com.supplychainx.service_user.dto.Response.RoleResponseDTO;
import com.supplychainx.service_user.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<RoleResponseDTO> getAllRoles() {
        return roleService.getAll();
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRole(
            @Valid @RequestBody RoleRequestDTO roleRequestDTO) {
        RoleResponseDTO createdDTO = roleService.create(roleRequestDTO);
        return GlobalSuccessHandler.handleSuccessWithData("Role created successfully", createdDTO);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public RoleResponseDTO getRoleById(
            @PathVariable("id") Long id) {
        return roleService.getById(id);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRole(
            @PathVariable("id") Long id, @Valid @RequestBody RoleRequestDTO roleRequestDTO) {
        RoleResponseDTO updatedDTO = roleService.update(id, roleRequestDTO);
        return GlobalSuccessHandler.handleSuccessWithData("Role updated successfully", updatedDTO);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRole(
            @PathVariable("id") Long id) {
        roleService.delete(id);
        return GlobalSuccessHandler.handleDeleted("Role deleted successfully");
    }

}