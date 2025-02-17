package com.seungjoon.algo.study.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateStudyRequest {

    @NotNull
    private Long recruitPostId;

    @NotNull
    private List<Long> memberIds;
}
