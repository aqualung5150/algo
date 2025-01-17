package com.seungjoon.algo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {

    NOT_FOUND_USER(1001, "존재하지 않는 유저입니다."),
    INTERNAL_SEVER_ERROR(9999, "서버 에러가 발생하였습니다.");

    private final int code;
    private final String message;
}
