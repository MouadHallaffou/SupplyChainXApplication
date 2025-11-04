package com.supplychainx.service_user.controller;

import com.supplychainx.handler.GlobalSuccessHandler;
import com.supplychainx.service_user.dto.RoleRequestDTO;
import com.supplychainx.service_user.dto.RoleResponseDTO;
import com.supplychainx.service_user.service.RoleService;
import com.supplychainx.util.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    private final AuthUtil authUtil;

    @GetMapping
    public List<RoleResponseDTO> getAllRoles() {
        return roleService.getAll();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createRole(
            @RequestHeader("email") String email,
            @RequestHeader("password") String password,
            @Valid @RequestBody RoleRequestDTO roleRequestDTO) {
        authUtil.verifyAccess(email, password, "ADMIN");
        RoleResponseDTO createdDTO = roleService.create(roleRequestDTO);
        return GlobalSuccessHandler.handleSuccessWithData("Role created successfully", createdDTO);
    }

    @GetMapping("/{id}")
    public RoleResponseDTO getRoleById(@PathVariable("id") Long id) {
        return roleService.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String,Object>> updateRole(@PathVariable("id") Long id, @Valid @RequestBody RoleRequestDTO roleRequestDTO) {
        RoleResponseDTO updatedDTO = roleService.update(id, roleRequestDTO);
        return GlobalSuccessHandler.handleSuccessWithData("Role updated successfully", updatedDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRole(@PathVariable("id") Long id) {
        roleService.delete(id);
        return GlobalSuccessHandler.handleDeleted("Role deleted successfully");
    }

}