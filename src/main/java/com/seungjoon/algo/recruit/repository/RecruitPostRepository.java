package com.seungjoon.algo.recruit.repository;

import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.recruit.dto.RecruitPostSliceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RecruitPostRepository extends JpaRepository<RecruitPost, Long>, RecruitPostRepositoryCustom {

    @Query("select r from RecruitPost r" +
            " join fetch r.member" +
            " join fetch r.studyRule sr" +
            " where r.id = :id")
    Optional<RecruitPost> findByIdJoinFetch(Long id);

    @Query("select r from RecruitPost r" +
            " join fetch r.member m" +
            " join fetch r.studyRule sr" +
            " where m.id = :memberId")
    Page<RecruitPost> findByMemberIdJoinFetch(Long memberId, Pageable pageable);

    @Query("select r from Applicant a join a.recruitPost r where a.member.id = :memberId")
    Slice<RecruitPost> findByApplicantMemberId(Long memberId, Pageable pageable);
}
