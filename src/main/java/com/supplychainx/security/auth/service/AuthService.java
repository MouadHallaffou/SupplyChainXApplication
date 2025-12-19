package com.supplychainx.security.auth.service;

import com.supplychainx.security.auth.dto.JwtResponseDto;
import com.supplychainx.security.auth.dto.LoginRequestDto;
import com.supplychainx.security.auth.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequestDto loginRequest);
    void logout(String token);
    JwtResponseDto refreshToken(String refreshToken);
}