package com.supplychainx.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class GlobalSuccessHandler {

    public static ResponseEntity<Map<String, Object>> handleSuccess(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        return new ResponseEntity<>(response, status);
    }

    public static ResponseEntity<Map<String, Object>> handleSuccessWithData(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.OK.value());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static ResponseEntity<Map<String, Object>> handleSuccessWithDataCreated(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CREATED.value());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public static ResponseEntity<Map<String, Object>> handleDeleted(String message) {
        return handleSuccess(message, HttpStatus.OK);
    }

    public static ResponseEntity<Map<String, Object>> handleActivate(String message) {
        return handleSuccess(message, HttpStatus.OK);
    }
}
