package com.seungjoon.algo.exception;

import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {
    private final int code;
    private final String message;

    public UnauthorizedException(ExceptionCode exceptionCode) {
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMessage();
    }
}
