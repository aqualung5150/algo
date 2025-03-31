package com.seungjoon.algo.submission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateSubmissionRequest {

    @NotNull
    Integer subjectNumber;

    @NotBlank
    String content;

    @NotBlank
    //TODO: @Pattern
    String visibility;

    @NotNull
    List<Long> tags;
}
