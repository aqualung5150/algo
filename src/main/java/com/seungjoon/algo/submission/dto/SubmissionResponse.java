package com.seungjoon.algo.submission.dto;

import com.seungjoon.algo.member.dto.ProfileResponse;
import com.seungjoon.algo.submission.domain.Submission;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SubmissionResponse {

    private Long submissionId;
    private Integer subjectNumber;
    private ProfileResponse profile;
    private String content;
    private String visibility;
    private Integer weekNumber;
    private List<Long> tags;

    public static SubmissionResponse from(Submission submission) {

        return new SubmissionResponse(
                submission.getId(),
                submission.getSubjectNumber(),
                ProfileResponse.from(submission.getMember()),
                submission.getContent(),
                submission.getVisibility().name(),
                submission.getWeekNumber(),
                submission.getTags().stream().mapToLong(st -> st.getTag().getId()).boxed().toList()
        );
    }
}
