package com.supplychainx.security.auth;

public record JwtResponseDto (
        String accessToken,
        String refreshToken
) {
}
