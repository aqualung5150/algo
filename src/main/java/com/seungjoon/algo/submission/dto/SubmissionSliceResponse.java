package com.seungjoon.algo.submission.dto;

import com.seungjoon.algo.submission.domain.Submission;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SubmissionSliceResponse {
    private boolean hasNext;
    private List<SubmissionSummaryResponse> submissions;

    public static SubmissionSliceResponse of(boolean hasNext, List<Submission> submissions) {
        return new SubmissionSliceResponse(
                hasNext,
                submissions.stream().map(SubmissionSummaryResponse::from).toList()
        );
    }
}
