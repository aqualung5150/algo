package com.seungjoon.algo.auth.oauth;

import lombok.Builder;

public record PrincipalDTO(Long id, String password, String username, String name, String role) {

    @Builder
    public PrincipalDTO {
    }
}
