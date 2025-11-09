package com.supplychainx.service_user.controller;

import com.supplychainx.handler.GlobalSuccessHandler;
import com.supplychainx.service_user.dto.Request.UserRequestDTO;
import com.supplychainx.service_user.dto.Response.UserResponseDTO;
import com.supplychainx.service_user.service.UserService;
import com.supplychainx.util.AuthUtil;
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
    private final AuthUtil authUtil;

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAll();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestHeader("email") String email,
                                                          @RequestHeader("password") String password,
                                                          @Valid @RequestBody UserRequestDTO userRequestDTO) {
        authUtil.verifyAccess(email, password, "ADMIN");
        UserResponseDTO createdDTO = userService.create(userRequestDTO);
        return GlobalSuccessHandler.handleSuccessWithData("User created successfully", createdDTO);
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@RequestHeader("email") String email,
                                       @RequestHeader("password") String password,
                                       @PathVariable("id") Long id) {
        authUtil.verifyAccess(email, password, "ADMIN");
        return userService.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestHeader("email") String email, @RequestHeader("password") String password,
                                                          @PathVariable("id") Long id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        authUtil.verifyAccess(email, password, "ADMIN");
        UserResponseDTO updatedDTO = userService.update(id, userRequestDTO);
        return GlobalSuccessHandler.handleSuccessWithData("User updated successfully", updatedDTO);
    }

    @PutMapping("/softDelete/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@RequestHeader("email") String email,
                                                          @RequestHeader("password") String password,
                                                          @PathVariable("id") Long id) {
        authUtil.verifyAccess(email, password, "ADMIN");
        userService.softDelete(id);
        return GlobalSuccessHandler.handleDeleted("User deleted successfully");
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<Map<String, Object>> deactivateUser(@RequestHeader("email") String email,
                                                              @RequestHeader("password") String password,
                                                              @PathVariable("id") Long id) {
        authUtil.verifyAccess(email, password, "ADMIN");
        userService.deactivate(id);
        return GlobalSuccessHandler.handleDeleted("User deactivated successfully");
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<Map<String, Object>> activateUser(@RequestHeader("email") String email,
                                                            @RequestHeader("password") String password,
                                                            @PathVariable("id") Long id) {
        authUtil.verifyAccess(email, password, "ADMIN");
        userService.activate(id);
        return GlobalSuccessHandler.handleActivate("User activated successfully");
    }

}