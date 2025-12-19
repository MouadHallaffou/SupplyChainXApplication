package com.supplychainx.security.auth;

import com.supplychainx.security.jwt.JwtService;
import com.supplychainx.service_user.model.User;
import com.supplychainx.service_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public LoginResponse login(LoginRequestDto loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateJwtToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUserId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(3600000)
                .build();
    }

    @Override
    @Transactional
    public void logout(String token) {
        String userEmail = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        refreshTokenService.deleteByUserId(user.getUserId());
    }

    @Override
    public JwtResponseDto refreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserId)
                .map(userId -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

                    UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                            .username(user.getEmail())
                            .password(user.getPassword())
                            .roles(user.getRole().getName())
                            .build();

                    String newAccessToken = jwtService.generateJwtToken(userDetails);

                    return new JwtResponseDto(newAccessToken, refreshToken);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token invalide"));
    }
}