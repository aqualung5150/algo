package com.seungjoon.algo.study.repository;

import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.study.domain.BanVote;
import com.seungjoon.algo.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BanVoteRepository extends JpaRepository<BanVote, Long> {
    boolean existsByStudyIdAndVoterIdAndTargetId(Long studyId, Long voterId, Long targetId);

    List<BanVote> findByTarget(Member target);

    @Modifying
    @Query("delete from BanVote b where b.study.id = :studyId")
    void deleteByStudyId(Long studyId);
}
