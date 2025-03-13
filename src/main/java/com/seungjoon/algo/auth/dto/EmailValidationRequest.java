package com.seungjoon.algo.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmailValidationRequest {

    @NotNull
    private String email;
}
