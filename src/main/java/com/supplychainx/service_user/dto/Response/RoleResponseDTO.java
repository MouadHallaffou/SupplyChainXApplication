package com.supplychainx.service_user.dto.Response;

import java.time.LocalDateTime;

public record RoleResponseDTO(
        Long roleId,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
