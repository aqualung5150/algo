package com.seungjoon.algo.evaluation.dto;

import com.seungjoon.algo.evaluation.domain.PassFail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateEvaluationRequest {

    @NotBlank
    private String content;
    @NotNull
    //TODO: @Pattern
    private PassFail passFail;
}
