package com.seungjoon.algo.study.dto;

import com.seungjoon.algo.member.dto.ProfileResponse;
import lombok.Data;

import java.util.List;

@Data
public class StudyResponse {

    private Long id;
    private List<ProfileResponse> members;
    private StudyRuleResponse studyRule;
}
