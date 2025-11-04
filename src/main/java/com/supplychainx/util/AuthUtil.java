package com.supplychainx.util;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_user.model.User;
import com.supplychainx.service_user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {
    private final UserService userService;

    public void verifyAccess(String email, String password, String requiredRole) {
        User user = userService.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            throw new ResourceNotFoundException("Invalid password");
        }

        String userRole = user.getRole().getName().toUpperCase();
        if (!userRole.equals(requiredRole.toUpperCase())) {
            throw new ResourceNotFoundException("Access denied: required role = " + requiredRole);
        }
    }
}
