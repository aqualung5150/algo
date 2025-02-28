package com.seungjoon.algo.study.dto;

import com.seungjoon.algo.submission.domain.Tag;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SubmissionResponse {

    Long submissionId;
    Integer subjectNumber;
    String content;
    String visibility;
    //TODO: TEST 컨버전 해주나
    LocalDate submissionDate;
    List<Tag> tags;
}
