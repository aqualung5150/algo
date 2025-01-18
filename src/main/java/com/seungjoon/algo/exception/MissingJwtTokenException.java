package com.seungjoon.algo.exception;

public class MissingJwtTokenException extends UnauthorizedException{
    public MissingJwtTokenException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
