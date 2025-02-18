package com.seungjoon.algo.study.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CreateStudyRequest {

    @NotNull
    private Long recruitPostId;

    @NotNull
    private List<Long> memberIds;
}
