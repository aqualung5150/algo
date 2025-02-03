package com.seungjoon.algo.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateMemberRequest {

    @NotBlank
    String email;
    @NotBlank
    String username;

    String imageUrl;
}
