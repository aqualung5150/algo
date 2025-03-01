package com.seungjoon.algo.submission.dto;

import com.seungjoon.algo.submission.domain.Submission;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SubmissionResponse {

    private Long submissionId;
    private Integer subjectNumber;
    private String content;
    private String visibility;
    private LocalDate submissionDate;
    private List<Long> tags;

    public static SubmissionResponse from(Submission submission) {

        return new SubmissionResponse(
                submission.getId(),
                submission.getSubjectNumber(),
                submission.getContent(),
                submission.getVisibility().name(),
                submission.getSubmitDate(),
                submission.getTags().stream().mapToLong(st -> st.getTag().getId()).boxed().toList()
        );
    }
}
