package com.seungjoon.algo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {

    BAD_REQUEST(1000, "잘못된 요청입니다."),
    NOT_FOUND_MEMBER(1001, "존재하지 않는 유저입니다."),
    INTERNAL_SEVER_ERROR(9999, "서버 에러가 발생하였습니다."),
    EXPIRED_JWT_TOKEN(1002, "토큰이 만료되었습니다."),
    INVALID_JWT_TOKEN(1003, "유효하지 않은 토큰입니다."),
    MISSING_JWT_TOKEN(1004, "토큰이 없습니다."),

    EMAIL_ALREADY_EXIST(1005, "존재하는 email입니다."),
    USERNAME_ALREADY_EXIST(1006, "존재하는 username입니다."),
    LOGIN_FAILED(1007, "로그인에 실패했습니다."),
    EXISTING_NORMAL_MEMBER(1008, "이미 가입한 일반계정이 있습니다."),
    EXISTING_GOOGLE_MEMBER(1009, "이미 Google로 가입한 계정이 있습니다."),
    EXISTING_NAVER_MEMBER(1010, "이미 Naver로 가입한 계정이 있습니다."),

    NOT_FOUND_POST(1011, "존재하지 않는 게시글입니다."),
    DUPLICATE_APPLICANT(1012, "이미 지원한 모집글입니다."),
    SAME_AUTHOR_APPLICANT(1013, "자신의 모집글에 지원할 수 없습니다."),
    NOT_FOUND_APPLICANT(1014, "해당 모집글에 지원하지 않았습니다."),
    NOT_OWN_RESOURCE(1015, "해당 자원의 소유자가 아닙니다."),
    INVALID_TAGS(1016, "올바르지 않은 태그가 포함되어 있습니다."),
    INVALID_NUMBER_OF_MEMBERS(1017, "정해진 회원 수에 맞지 않습니다."),
    INVALID_APPLICANTS_SELECTION(1018, "지원하지 않은 멤버입니다."),
    RECRUITMENT_FINISHED(1019, "모집이 완료된 글입니다."),
    NOT_FOUND_STUDY(1020, "존재하지 않는 스터디입니다."),
    DUPLICATE_CLOSING_VOTE(1021, "이미 스터디 종료에 투표했습니다."),
    DUPLICATE_BAN_VOTE(1022, "이미 해당 팀원에게 강퇴 투표했습니다."),
    STUDY_CLOSED(1023, "해당 스터디는 종료되었습니다."),
    MEMBER_NOT_IN_STUDY(1024, "스터디의 팀원이 아닙니다."),
    SAME_VOTER_TARGET(1025, "자신에게 투표할 수 없습니다."),
    PRIVATE_POST(1026, "비공개 게시글입니다."),
    DUPLICATE_EVALUATION(1027, "이미 평가가 존재합니다."),
    NOT_FOUND_SUBMISSION(1028, "존재하지 않는 제출입니다."),
    PASSED_SUBMISSION(1029, "이미 통과한 제출입니다.");

//    INVALID_PASSWORD(1007, "패스워드가 일치하지 않습니다.");

//    NOT_FOUND(1005, "요청한 페이지를 찾을 수 없습니다.")

    private final int code;
    private final String message;
}
