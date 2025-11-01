package com.supplychainx.util;

import com.supplychainx.service_user.model.User;
import com.supplychainx.service_user.service.UserServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    private final UserServiceImpl userServiceImpl;

    public AuthUtil(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    public User verifyCredentials(String email, String password, String requiredRole) {
        return null;
    }
}

