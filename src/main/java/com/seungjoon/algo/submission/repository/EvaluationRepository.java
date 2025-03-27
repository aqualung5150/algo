package com.seungjoon.algo.submission.repository;

import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.submission.domain.Evaluation;
import com.seungjoon.algo.submission.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {


    Boolean existsBySubmissionAndMember(Submission submission, Member evaluator);

    List<Evaluation> findBySubmission(Submission submission);

    @Query("select e from Evaluation e where e.submission.id = :submissionId and e.member.id = :memberId")
    Optional<Evaluation> findBySubmissionIdAndMemberId(Long submissionId, Long memberId);
}
