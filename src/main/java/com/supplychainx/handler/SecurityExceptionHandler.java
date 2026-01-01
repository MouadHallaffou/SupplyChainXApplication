package com.supplychainx.handler;


import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;

@RestControllerAdvice
@Slf4j
public class SecurityExceptionHandler {

    private static final Logger SECURITY_AUDIT = LoggerFactory.getLogger("SECURITY_AUDIT");

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "anonymous";

        // Log structuré pour audit
        SECURITY_AUDIT.warn("Authentication failed",
                StructuredArguments.keyValue("event", "auth_failed"),
                StructuredArguments.keyValue("username", username),
                StructuredArguments.keyValue("uri", request.getRequestURI()),
                StructuredArguments.keyValue("method", request.getMethod()),
                StructuredArguments.keyValue("ip", request.getRemoteAddr()),
                StructuredArguments.keyValue("error", ex.getMessage())
        );

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(ZonedDateTime.now().toString())
                .status(401)
                .error("Unauthorized")
                .message("Access token invalide ou expiré")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(401).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "anonymous";

        String roles = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString()
                : "[]";

        // Log structuré pour audit
        SECURITY_AUDIT.warn("Access denied",
                StructuredArguments.keyValue("event", "access_denied"),
                StructuredArguments.keyValue("username", username),
                StructuredArguments.keyValue("roles", roles),
                StructuredArguments.keyValue("uri", request.getRequestURI()),
                StructuredArguments.keyValue("method", request.getMethod()),
                StructuredArguments.keyValue("ip", request.getRemoteAddr())
        );

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(ZonedDateTime.now().toString())
                .status(403)
                .error("Forbidden")
                .message("Autorisations insuffisantes")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(403).body(error);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(
            JwtException ex, HttpServletRequest request) {

        // Log structuré pour audit
        SECURITY_AUDIT.warn("JWT validation failed",
                StructuredArguments.keyValue("event", "jwt_invalid"),
                StructuredArguments.keyValue("uri", request.getRequestURI()),
                StructuredArguments.keyValue("method", request.getMethod()),
                StructuredArguments.keyValue("ip", request.getRemoteAddr()),
                StructuredArguments.keyValue("error", ex.getMessage())
        );

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(ZonedDateTime.now().toString())
                .status(401)
                .error("Unauthorized")
                .message("Token JWT invalide")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(401).body(error);
    }
}