package com.seungjoon.algo.submission.repository;

import com.seungjoon.algo.submission.domain.Submission;
import com.seungjoon.algo.submission.dto.SubmissionCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubmissionRepositoryCustom {

    Page<Submission> findAllByCondition(SubmissionCondition condition, Pageable pageable);
}
