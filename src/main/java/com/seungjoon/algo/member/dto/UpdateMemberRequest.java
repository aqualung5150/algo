package com.seungjoon.algo.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateMemberRequest {

    @NotBlank
    @Size(min = 2, message = "2자 이상이어야 합니다.")
    @Size(max = 12, message = "최대 12자까지 가능합니다.")
    private String username;

    @NotNull
    private String imageUrl;
}
