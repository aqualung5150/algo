package com.seungjoon.algo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ExceptionResponse {
    private final int code;
    private final String message;
}
