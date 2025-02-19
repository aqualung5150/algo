package com.seungjoon.algo.study.repository;

import com.seungjoon.algo.study.domain.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyRepository extends JpaRepository<Study, Long> {

    @Query("select s from Study s join s.studyMembers sm where sm.member.id = :memberId")
    Page<Study> findByMemberId(Long memberId, Pageable pageable);
}
