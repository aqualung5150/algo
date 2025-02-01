package com.seungjoon.algo.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Getter
public class ExistingAuthTypeException extends AuthenticationException {
    private final int code;
    private final String message;

    public ExistingAuthTypeException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMessage();
    }
}
