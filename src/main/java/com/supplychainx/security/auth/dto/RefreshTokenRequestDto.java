package com.supplychainx.security.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDto {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
