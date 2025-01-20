package com.seungjoon.algo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
