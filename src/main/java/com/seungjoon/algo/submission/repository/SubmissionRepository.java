package com.seungjoon.algo.submission.repository;

import com.seungjoon.algo.submission.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long>, SubmissionRepositoryCustom {

    @Query("select s from Submission s join fetch s.member where s.id = :id")
    Optional<Submission> findByIdJoinFetchMember(Long id);

    @Query("select s from Submission s" +
            " join fetch s.study st" +
//            " join fetch st.studyRule" +
            " where s.id = :id")
    Optional<Submission> findByIdJoinFetchStudy(Long id);
}
