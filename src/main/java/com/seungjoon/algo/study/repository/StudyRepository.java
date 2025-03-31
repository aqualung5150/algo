package com.seungjoon.algo.study.repository;

import com.seungjoon.algo.study.domain.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {

    /* ToOne 페치조인은 한번만 가능함*/
    @Query("select s from Study s" +
            " join fetch s.studyRule sr" +
//            " join fetch sr.studyRuleTags srt" +
//            " join fetch srt.tag" +
            " join fetch s.studyMembers sm" +
            " join fetch sm.member m" +
            " where s.id = :id")
    Optional<Study> findByIdJoinFetch(Long id);

    @Query("select s from Study s join s.studyMembers sm where sm.member.id = :memberId")
    Page<Study> findByMemberId(Long memberId, Pageable pageable);

    List<Study> findByLastSubmitDateBefore(LocalDate localDate);
}
