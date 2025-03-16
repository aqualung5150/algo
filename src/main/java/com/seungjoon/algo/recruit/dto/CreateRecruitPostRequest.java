package com.seungjoon.algo.recruit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Getter
@AllArgsConstructor
public class CreateRecruitPostRequest {

    @NotBlank
    private String title;

    private String content;

    @NotNull
    @Range(min = 2, max = 4)
    private Integer numberOfMembers;

    @NotNull
    @Range(min = 1, max = 5)
    private Integer level;

    @NotNull
    private Integer totalWeek;

    @NotBlank
    @Pattern(regexp = "^(MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY)$",
            message = "MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY 중 하나를 입력하세요.")
    private String submitDayOfWeek;

    @NotNull
    private Integer submitPerWeek;

    @NotNull
    private List<Long> tags;
}
