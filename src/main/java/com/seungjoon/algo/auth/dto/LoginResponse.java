package com.seungjoon.algo.auth.dto;

import com.seungjoon.algo.member.dto.ProfileResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginResponse {
    private String redirectUrl;
    private ProfileResponse profile;
}
