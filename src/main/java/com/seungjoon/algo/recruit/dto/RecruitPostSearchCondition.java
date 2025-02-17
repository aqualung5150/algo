package com.seungjoon.algo.recruit.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecruitPostSearchCondition {

    private String title;
    private List<Long> tag;
    private Integer minLevel;
    private Integer maxLevel;
}
