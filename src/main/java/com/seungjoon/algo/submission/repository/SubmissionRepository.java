package com.seungjoon.algo.submission.repository;

import com.seungjoon.algo.submission.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}
