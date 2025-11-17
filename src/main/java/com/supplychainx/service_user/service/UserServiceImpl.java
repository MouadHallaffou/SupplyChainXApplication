package com.supplychainx.service_user.service;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_user.dto.Request.UserRequestDTO;
import com.supplychainx.service_user.dto.Response.UserResponseDTO;
import com.supplychainx.service_user.mapper.UserMapper;
import com.supplychainx.service_user.model.Role;
import com.supplychainx.service_user.model.User;
import com.supplychainx.service_user.repository.RoleRepository;
import com.supplychainx.service_user.repository.UserRepository;
import com.supplychainx.util.PasswordUtil;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserResponseDTO create(UserRequestDTO userRequestDTO) {
        User user = userMapper.toEntity(userRequestDTO);
        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        Role role = roleRepository.findById(userRequestDTO.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + userRequestDTO.getRoleId()));
        user.setRole(role);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponseDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO userRequestDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userMapper.updateEntityFromDTO(userRequestDTO, existingUser);
        existingUser.setPassword(PasswordUtil.hashPassword(userRequestDTO.getPassword()));
        // gère le rôle
        Role role = roleRepository.findById(userRequestDTO.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + userRequestDTO.getRoleId()));
        existingUser.setRole(role);
        existingUser.setUserId(id);
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        if (user.getIsDeleted() == true) {
            return;
        }
        user.setIsDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        if (user.getIsActive() == false) {
            return;
        }
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void activate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        if (user.getIsActive() == true) {
            return;
        }
        user.setIsActive(true);
        userRepository.save(user);
    }

    @Override
    public List<UserResponseDTO> getAll() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }
        return users.stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}