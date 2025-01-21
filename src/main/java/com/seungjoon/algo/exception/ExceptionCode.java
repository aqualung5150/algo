package com.seungjoon.algo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {

    BAD_REQUEST(1000, "잘못된 요청입니다."),
    NOT_FOUND_USER(1001, "존재하지 않는 유저입니다."),
    INTERNAL_SEVER_ERROR(9999, "서버 에러가 발생하였습니다."),
    EXPIRED_JWT_TOKEN(1002, "토큰이 만료되었습니다."),
    INVALID_JWT_TOKEN(1003, "유효하지 않은 토큰입니다."),
    MISSING_JWT_TOKEN(1004, "토큰이 없습니다."),

    EMAIL_ALREADY_EXIST(1005, "존재하는 email입니다."),
    USERNAME_ALREADY_EXIST(1006, "존재하는 username입니다."),
    INVALID_PASSWORD(1007, "패스워드가 일치하지 않습니다.");

//    NOT_FOUND(1005, "요청한 페이지를 찾을 수 없습니다.")

    private final int code;
    private final String message;
}
