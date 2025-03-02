package com.seungjoon.algo.submission.dto;

import lombok.Data;

@Data
public class SubmissionCondition {

    private Integer subjectNumber;
    private Long memberId;
    private Long studyId;
    private Integer weekNumber;
}
