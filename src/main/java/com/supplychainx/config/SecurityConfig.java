package com.supplychainx.config;

import com.supplychainx.security.CustomAccessDeniedHandler;
import com.supplychainx.security.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    @Value("${jwt.auth.converter.principle-attribute}")
    private String principleAttribute;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationEntryPoint customAuthenticationEntryPoint, CustomAccessDeniedHandler customAccessDeniedHandler) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/graphiql/**",
                                "/graphql"
                        ).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS));

        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setPrincipalClaimName(principleAttribute);

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            List<String> realmRoles = realmAccess != null && realmAccess.get("roles") != null
                    ? (List<String>) realmAccess.get("roles")
                    : List.of();

            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            List<String> clientRoles = List.of();

            if (resourceAccess != null && resourceAccess.containsKey(resourceId)) {
                Map<String, Object> clientResource = (Map<String, Object>) resourceAccess.get(resourceId);
                if (clientResource != null && clientResource.get("roles") != null) {
                    clientRoles = (List<String>) clientResource.get("roles");
                }
            }

            return Stream.concat(realmRoles.stream(), clientRoles.stream())
                    .distinct()
                    .map(role -> "ROLE_" + role.toUpperCase())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });

        return converter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}