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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO createdDTO = userService.create(userRequestDTO);
        return GlobalSuccessHandler.handleSuccessWithDataCreated("User created successfully", createdDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable("id") Long id) {
        return userService.getById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO updatedDTO = userService.update(id, userRequestDTO);
        return GlobalSuccessHandler.handleSuccessWithData("User updated successfully", updatedDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/softDelete/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("id") Long id) {
        userService.softDelete(id);
        return GlobalSuccessHandler.handleDeleted("User deleted successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/deactivate/{id}")
    public ResponseEntity<Map<String, Object>> deactivateUser(@PathVariable("id") Long id) {
        userService.deactivate(id);
        return GlobalSuccessHandler.handleDeleted("User deactivated successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/activate/{id}")
    public ResponseEntity<Map<String, Object>> activateUser(@PathVariable("id") Long id) {
        userService.activate(id);
        return GlobalSuccessHandler.handleActivate("User activated successfully");
    }

}