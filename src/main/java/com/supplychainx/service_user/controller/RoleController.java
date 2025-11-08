package com.supplychainx.service_user.controller;

import com.supplychainx.handler.GlobalSuccessHandler;
import com.supplychainx.service_user.dto.Request.RoleRequestDTO;
import com.supplychainx.service_user.dto.Response.RoleResponseDTO;
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
    public List<RoleResponseDTO> getAllRoles(
            @RequestHeader("email") String email,
            @RequestHeader("password") String password) {
        authUtil.verifyAccess(email, password, "ADMIN");
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
    public RoleResponseDTO getRoleById(
            @RequestHeader("email") String email,
            @RequestHeader("password") String password,
            @PathVariable("id") Long id) {
        authUtil.verifyAccess(email, password, "ADMIN");
        return roleService.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRole(
            @RequestHeader("email") String email,
            @RequestHeader("password") String password,
            @PathVariable("id") Long id, @Valid @RequestBody RoleRequestDTO roleRequestDTO) {
        authUtil.verifyAccess(email, password, "ADMIN");
        RoleResponseDTO updatedDTO = roleService.update(id, roleRequestDTO);
        return GlobalSuccessHandler.handleSuccessWithData("Role updated successfully", updatedDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRole(
            @RequestHeader("email") String email,
            @RequestHeader("password") String password,
            @PathVariable("id") Long id) {
        authUtil.verifyAccess(email, password, "ADMIN");
        roleService.delete(id);
        return GlobalSuccessHandler.handleDeleted("Role deleted successfully");
    }

}