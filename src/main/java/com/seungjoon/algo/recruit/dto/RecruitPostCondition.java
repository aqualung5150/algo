package com.seungjoon.algo.recruit.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecruitPostCondition {

    private String title;
    private List<Long> tag;
    private Integer level;
    private String state;
}
