package com.seungjoon.algo.study.dto;

import com.seungjoon.algo.member.dto.ProfileResponse;
import com.seungjoon.algo.study.domain.StudyMember;
import lombok.Data;

@Data
public class StudyMemberResponse {

    private final ProfileResponse profile;

    private final String role;
    private final String state;
    private final Integer notSubmitted;

    public static StudyMemberResponse from(StudyMember studyMember) {

        return new StudyMemberResponse(
                ProfileResponse.from(studyMember.getMember()),
                studyMember.getRole().name(),
                studyMember.getState().name(),
                studyMember.getNotSubmitted()
        );
    }
}
