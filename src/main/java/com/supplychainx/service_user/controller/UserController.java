package com.supplychainx.service_user.controller;

import com.supplychainx.handler.GlobalSuccessHandler;
import com.supplychainx.service_user.dto.Request.UserRequestDTO;
import com.supplychainx.service_user.dto.Response.UserResponseDTO;
import com.supplychainx.service_user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('PLANIFICATEUR')")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAll();
    }

    @PreAuthorize("hasRole('PLANIFICATEUR')")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO createdDTO = userService.create(userRequestDTO);
        return GlobalSuccessHandler.handleSuccessWithDataCreated("User created successfully", createdDTO);
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable("id") Long id) {
        return userService.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO updatedDTO = userService.update(id, userRequestDTO);
        return GlobalSuccessHandler.handleSuccessWithData("User updated successfully", updatedDTO);
    }

    @PutMapping("/softDelete/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("id") Long id) {
        userService.softDelete(id);
        return GlobalSuccessHandler.handleDeleted("User deleted successfully");
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<Map<String, Object>> deactivateUser(@PathVariable("id") Long id) {
        userService.deactivate(id);
        return GlobalSuccessHandler.handleDeleted("User deactivated successfully");
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<Map<String, Object>> activateUser(@PathVariable("id") Long id) {
        userService.activate(id);
        return GlobalSuccessHandler.handleActivate("User activated successfully");
    }

}