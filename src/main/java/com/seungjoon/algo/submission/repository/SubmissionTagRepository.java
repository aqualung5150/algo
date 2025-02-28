package com.seungjoon.algo.submission.repository;

import com.seungjoon.algo.submission.domain.SubmissionTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionTagRepository extends JpaRepository<SubmissionTag, Long> {
}
