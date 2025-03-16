package com.seungjoon.algo.study.dto;


import com.seungjoon.algo.study.domain.StudyRule;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyRuleResponse {

    private int numberOfMembers;
    private int level;
    private int totalWeek;
    private String submitDayOfWeek;
    private int submitPerWeek;
    private List<Long> tags;

    public static StudyRuleResponse from(StudyRule studyRule) {
        return new StudyRuleResponse(
                studyRule.getNumberOfMembers(),
                studyRule.getLevel(),
                studyRule.getTotalWeek(),
                studyRule.getSubmitDayOfWeek().name(),
                studyRule.getSubmitPerWeek(),
                studyRule.getStudyRuleTags().stream()
                        .map(studyRuleTag -> studyRuleTag.getTag().getId())
                        .toList()
        );
    }
}
