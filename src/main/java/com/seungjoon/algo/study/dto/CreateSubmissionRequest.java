package com.seungjoon.algo.study.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateSubmissionRequest {

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
