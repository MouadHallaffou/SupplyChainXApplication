package com.supplychainx.service_user.service;

import com.supplychainx.service_user.dto.Request.UserRequestDTO;
import com.supplychainx.service_user.dto.Response.UserResponseDTO;
import com.supplychainx.service_user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponseDTO create(UserRequestDTO userRequestDTO);
    UserResponseDTO getById(Long id);
    UserResponseDTO update(Long id, UserRequestDTO userRequestDTO);
    void softDelete(Long id);
    void deactivate(Long id);
    void activate(Long id);
    List<UserResponseDTO> getAll();
    Optional<User> findByEmail(String email);
}
