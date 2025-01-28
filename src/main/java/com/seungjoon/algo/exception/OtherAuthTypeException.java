package com.seungjoon.algo.exception;

import org.springframework.security.core.AuthenticationException;

public class OtherAuthTypeException extends AuthenticationException {
    private final int code;
    private final String message;

    public OtherAuthTypeException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMessage();
    }
}
