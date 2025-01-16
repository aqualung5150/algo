package com.seungjoon.algo.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank
    String email;
    @NotBlank
    String username;

    String imageUrl;
}
