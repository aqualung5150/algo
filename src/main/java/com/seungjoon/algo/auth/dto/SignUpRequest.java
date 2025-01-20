package com.seungjoon.algo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignUpRequest {

    @NotBlank
    //TODO - @Pattern - 정규표현식
    private String email;

    @NotBlank
    @Size(min = 2, message = "2자 이상이어야 합니다.")
    @Size(max = 12, message = "최대 12자까지 가능합니다.")
    private String username;

    @NotBlank
    //TODO - @Pattern - 정규표현식
    private String password;
}
