package com.seungjoon.algo.study.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Data
@AllArgsConstructor
public class CreateStudyRequest {

    @NotNull
    private Long recruitPostId;

    @NotBlank
    private String name;

    @NotNull
    @Size(min = 2, max = 4, message = "스터디의 팀원 수는 2 ~ 4명 사이여야 합니다.")
    @UniqueElements
    private List<Long> memberIds;
}
