package com.supplychainx.security.auth;

public interface AuthService {
    LoginResponse login(LoginRequestDto loginRequest);
    void logout(String token);
    JwtResponseDto refreshToken(String refreshToken);
}