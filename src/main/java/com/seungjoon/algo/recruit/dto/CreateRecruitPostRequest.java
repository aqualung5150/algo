package com.seungjoon.algo.recruit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class CreateRecruitPostRequest {

    @NotNull
    private Long authorId;

    @NotBlank
    private String title;

    private String content;

    @NotNull
    private Integer totalWeek;
    @NotBlank
    @Pattern(regexp = "^(MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY)$", message = "MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY 중 하나를 입력하세요.")
    private String submitDayOfWeek;
    @NotNull
    private Integer submitPerWeek;
}
