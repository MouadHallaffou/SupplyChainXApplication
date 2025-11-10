package com.supplychainx.service_user.dto.Response;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long userId,
        String firstName,
        String lastName,
        String email,
        Boolean isActive,
        Boolean isDeleted,
        String roleName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
