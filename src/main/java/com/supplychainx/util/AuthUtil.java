package com.supplychainx.util;

import com.supplychainx.service_user.model.User;
import com.supplychainx.service_user.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    private final UserService userService;

    public AuthUtil(UserService userService) {
        this.userService = userService;
    }

    public User verifyCredentials(String email, String password, String requiredRole) {
        return null;
    }
}

