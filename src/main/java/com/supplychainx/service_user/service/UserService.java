package com.supplychainx.service_user.service;

import com.supplychainx.service_user.dto.UserRequestDTO;
import com.supplychainx.service_user.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO create(UserRequestDTO userRequestDTO);
    UserResponseDTO getById(Long id);
    UserResponseDTO update(Long id, UserRequestDTO userRequestDTO);
    void delete(Long id);
    List<UserResponseDTO> getAll();
}
