package com.seungjoon.algo.auth.oauth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SetUsernameRequest {

    @NotBlank
    @Size(min = 2, message = "2자 이상이어야 합니다.")
    @Size(max = 12, message = "최대 12자까지 가능합니다.")
    private String username;
}
