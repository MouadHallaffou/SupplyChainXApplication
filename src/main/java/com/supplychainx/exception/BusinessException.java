package com.supplychainx.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private String errorCode;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}