package com.seungjoon.algo.submission.dto;

import com.seungjoon.algo.submission.domain.Submission;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SubmissionPageResponse {

    private Long totalCount;
    private List<SubmissionSummaryResponse> submissions;

    public static SubmissionPageResponse of(Long totalCount, List<Submission> submissions) {
        return new SubmissionPageResponse(
                totalCount,
                submissions.stream().map(SubmissionSummaryResponse::from).toList()
        );
    }
}
