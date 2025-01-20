package com.seungjoon.algo.auth;

import lombok.Builder;

public record PrincipalDto(Long id, String password, String username, String name, String role) {

    @Builder
    public PrincipalDto {
    }
}
