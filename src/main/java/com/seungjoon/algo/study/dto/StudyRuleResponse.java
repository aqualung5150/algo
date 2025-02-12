package com.seungjoon.algo.study.dto;


import com.seungjoon.algo.study.domain.StudyRule;
import com.seungjoon.algo.study.domain.StudyRuleTag;
import com.seungjoon.algo.subject.domain.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyRuleResponse {

    private int totalWeek;
    private String submitDayOfWeek;
    private int submitPerWeek;
    private List<String> tags;

    public static StudyRuleResponse from(StudyRule studyRule) {
        return new StudyRuleResponse(
                studyRule.getTotalWeek(),
                studyRule.getSubmitDayOfWeek().name(),
                studyRule.getSubmitPerWeek(),
                studyRule.getStudyRuleTags().stream()
                        .map(studyRuleTag -> studyRuleTag.getTag().getName())
                        .toList()
        );
    }
}
