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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.ZonedDateTime;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger SECURITY_AUDIT = LoggerFactory.getLogger("SECURITY_AUDIT");
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        String username = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "anonymous";

        String roles = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString()
                : "[]";

        // Log structuré pour audit
        SECURITY_AUDIT.warn("Access forbidden",
                StructuredArguments.keyValue("event", "access_forbidden"),
                StructuredArguments.keyValue("username", username),
                StructuredArguments.keyValue("roles", roles),
                StructuredArguments.keyValue("uri", request.getRequestURI()),
                StructuredArguments.keyValue("method", request.getMethod()),
                StructuredArguments.keyValue("ip", request.getRemoteAddr())
        );

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(ZonedDateTime.now().toString())
                .status(403)
                .error("Forbidden")
                .message("Autorisations insuffisantes pour accéder à cette ressource")
                .path(request.getRequestURI())
                .build();

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}