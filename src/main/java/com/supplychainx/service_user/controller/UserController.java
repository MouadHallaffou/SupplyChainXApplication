package com.supplychainx.service_user.controller;

import com.supplychainx.handler.GlobalSuccessHandler;
import com.supplychainx.service_user.dto.UserRequestDTO;
import com.supplychainx.service_user.dto.UserResponseDTO;
import com.supplychainx.service_user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAll();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO createdDTO = userService.create(userRequestDTO);
        return GlobalSuccessHandler.handleSuccessWithData("User created successfully", createdDTO);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
        return GlobalSuccessHandler.handleDeleted("User deleted successfully");
    }
}