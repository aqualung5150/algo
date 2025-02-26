package com.seungjoon.algo.study.repository;

import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.study.domain.ClosingVote;
import com.seungjoon.algo.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClosingVoteRepository extends JpaRepository<ClosingVote, Long> {

    @Query("select count(c) from ClosingVote c where c.study.id = :studyId")
    Long countByStudyId(Long studyId);

    @Modifying
    @Query("delete from ClosingVote c where c.study.id = :studyId")
    void deleteByStudyId(Long studyId);

    @Query("select c from ClosingVote c join fetch c.member where c.study.id = :studyId")
    List<ClosingVote> findByStudyIdJoinFetch(Long studyId);
}
