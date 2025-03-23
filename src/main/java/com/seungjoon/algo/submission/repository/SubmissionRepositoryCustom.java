package com.seungjoon.algo.submission.repository;

import com.seungjoon.algo.submission.domain.Submission;
import com.seungjoon.algo.submission.dto.SubmissionCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface SubmissionRepositoryCustom {

    Page<Submission> findPageByCondition(SubmissionCondition condition, Pageable pageable);
    Slice<Submission> findSliceByCondition(SubmissionCondition condition, Pageable pageable);
}
