package com.supplychainx.security.auth.dto;

public record JwtResponseDto (
        String accessToken,
        String refreshToken
) {
}
