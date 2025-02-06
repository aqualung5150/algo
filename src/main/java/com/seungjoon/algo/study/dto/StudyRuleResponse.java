package com.seungjoon.algo.study.dto;


import com.seungjoon.algo.study.domain.StudyRule;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyRuleResponse {

    private int totalWeek;
    private String submitDayOfWeek;
    private int submitPerWeek;

    public static StudyRuleResponse from(StudyRule studyRule) {
        return new StudyRuleResponse(
                studyRule.getTotalWeek(),
                studyRule.getSubmitDayOfWeek().name(),
                studyRule.getSubmitPerWeek()
        );
    }
}
