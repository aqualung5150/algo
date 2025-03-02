package com.seungjoon.algo.submission.dto;

import com.seungjoon.algo.submission.domain.PassFail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateEvaluationRequest {

    @NotBlank
    private String content;

    @NotNull
    //TODO: @Pattern
    private String passFail;
}
