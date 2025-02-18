package com.seungjoon.algo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {

    @NotBlank
    @Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "잘못된 이메일 형식입니다.")
    private String email;

    @NotBlank
    @Size(min = 2, message = "2자 이상이어야 합니다.")
    @Size(max = 12, message = "최대 12자까지 가능합니다.")
    private String username;

    @NotBlank
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*?_])(?=\\S+$).{8,20}$", message = "대문자, 소문자, 특수문자를 포함한 8~12자리 비밀번호여야 합니다.")
    private String password;
}
