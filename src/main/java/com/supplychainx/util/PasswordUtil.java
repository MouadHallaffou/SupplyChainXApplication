package com.supplychainx.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    private final static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    // hash a password using BCrypt
    public static String hashPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    // verify a password
    public static boolean verifyPassword(String rawPassword, String hashedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, hashedPassword);
    }

}