package com.supplychainx.security.auth;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
}