package com.supplychainx.service_user.controller;

import com.supplychainx.service_user.dto.UserRequestDTO;
import com.supplychainx.service_user.dto.UserResponseDTO;
import com.supplychainx.service_user.mapper.UserMapper;
import com.supplychainx.service_user.model.User;
import com.supplychainx.service_user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    @PostMapping
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        User user = userMapper.toEntity(userRequestDTO);
        User createdUser = userService.create(user);
        return userMapper.toResponseDTO(createdUser);
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        return userMapper.toResponseDTO(user);
    }

    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        User existingUser = userService.getById(id);
        User userToUpdate = userMapper.toEntity(userRequestDTO);
        userToUpdate.setUserId(existingUser.getUserId());
        User updatedUser = userService.update(userToUpdate);
        return userMapper.toResponseDTO(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
