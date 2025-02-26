package com.seungjoon.algo.study.repository;

import com.seungjoon.algo.study.domain.BanVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BanVoteRepository extends JpaRepository<BanVote, Long> {
    boolean existsByStudyIdAndVoterIdAndTargetId(Long studyId, Long voterId, Long targetId);
}
