package com.seungjoon.algo.study.dto;

import com.seungjoon.algo.study.domain.Study;
import lombok.Data;

import java.util.List;

@Data
public class StudyResponse {

    private final Long id;
    private final StudyRuleResponse studyRule;
    private final List<StudyMemberResponse> members;

    public static StudyResponse from(Study study) {

        List<StudyMemberResponse> members = study.getStudyMembers().stream()
                .map(StudyMemberResponse::from)
                .toList();

        return new StudyResponse(
                study.getId(),
                StudyRuleResponse.from(study.getStudyRule()),
                members
        );
    }
}
