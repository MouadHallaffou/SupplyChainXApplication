package com.supplychainx.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.supplychainx.handler.ErrorResponse;

import java.io.IOException;
import java.time.ZonedDateTime;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger SECURITY_AUDIT = LoggerFactory.getLogger("SECURITY_AUDIT");
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // Log structuré pour audit
        SECURITY_AUDIT.warn("Unauthorized access attempt",
                StructuredArguments.keyValue("event", "unauthorized_access"),
                StructuredArguments.keyValue("uri", request.getRequestURI()),
                StructuredArguments.keyValue("method", request.getMethod()),
                StructuredArguments.keyValue("ip", request.getRemoteAddr()),
                StructuredArguments.keyValue("userAgent", request.getHeader("User-Agent")),
                StructuredArguments.keyValue("error", authException.getMessage())
        );

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(ZonedDateTime.now().toString())
                .status(401)
                .error("Unauthorized")
                .message("Access token absent, invalide ou expiré")
                .path(request.getRequestURI())
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}