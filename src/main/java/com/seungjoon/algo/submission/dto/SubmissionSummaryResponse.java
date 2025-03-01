package com.seungjoon.algo.submission.dto;

import com.seungjoon.algo.member.dto.ProfileResponse;
import com.seungjoon.algo.submission.domain.Submission;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SubmissionSummaryResponse {

    private Long submissionId;
    private Integer subjectNumber;
    private ProfileResponse profile;
    private String visibility;
    private Integer weekNumber;

    public static SubmissionSummaryResponse from(Submission submission) {
        return new SubmissionSummaryResponse(
                submission.getId(),
                submission.getSubjectNumber(),
                ProfileResponse.from(submission.getMember()),
                submission.getVisibility().name(),
                submission.getWeekNumber()
        );
    }
}
