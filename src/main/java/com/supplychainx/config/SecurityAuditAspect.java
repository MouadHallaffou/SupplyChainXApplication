package com.supplychainx.config;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class SecurityAuditAspect {

    private static final Logger SECURITY_AUDIT = LoggerFactory.getLogger("SECURITY_AUDIT");

    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object auditApiCall(ProceedingJoinPoint joinPoint) throws Throwable {

        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";
        String roles = auth != null ? auth.getAuthorities().toString() : "[]";

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();

        String httpMethod = request.getMethod();
        String uri = request.getRequestURI();
        String ipAddress = getClientIpAddress(request);

        long startTime = System.currentTimeMillis();

        SECURITY_AUDIT.info("API Access Attempt",
                StructuredArguments.keyValue("event", "api_access"),
                StructuredArguments.keyValue("requestId", requestId),
                StructuredArguments.keyValue("username", username),
                StructuredArguments.keyValue("roles", roles),
                StructuredArguments.keyValue("httpMethod", httpMethod),
                StructuredArguments.keyValue("uri", uri),
                StructuredArguments.keyValue("ipAddress", ipAddress),
                StructuredArguments.keyValue("timestamp", Instant.now().toString())
        );

        try {
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - startTime;

            SECURITY_AUDIT.info("API Access Success",
                    StructuredArguments.keyValue("event", "api_success"),
                    StructuredArguments.keyValue("requestId", requestId),
                    StructuredArguments.keyValue("username", username),
                    StructuredArguments.keyValue("duration", duration),
                    StructuredArguments.keyValue("httpMethod", httpMethod),
                    StructuredArguments.keyValue("uri", uri)
            );

            return result;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;

            SECURITY_AUDIT.error("API Access Failed",
                    StructuredArguments.keyValue("event", "api_failure"),
                    StructuredArguments.keyValue("requestId", requestId),
                    StructuredArguments.keyValue("username", username),
                    StructuredArguments.keyValue("duration", duration),
                    StructuredArguments.keyValue("errorType", e.getClass().getSimpleName()),
                    StructuredArguments.keyValue("errorMessage", e.getMessage())
            );

            throw e;

        } finally {
            MDC.clear();
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

}