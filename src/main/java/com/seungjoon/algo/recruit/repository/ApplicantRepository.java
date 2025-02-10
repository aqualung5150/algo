package com.seungjoon.algo.recruit.repository;

import com.seungjoon.algo.recruit.domain.Applicant;
import com.seungjoon.algo.recruit.domain.ApplicantId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ApplicantRepository extends JpaRepository<Applicant, ApplicantId> {

    boolean existsByRecruitPostIdAndMemberId(Long postId, Long memberId);

    @Query(value = "select a from Applicant a join fetch a.member where a.recruitPost.id = :postId")
    Slice<Applicant> findAllByPostIdJoinFetchMember(Long postId, Pageable pageable);

    @Query(value = "select a from Applicant a join fetch a.recruitPost where a.member.id = :memberId")
    Slice<Applicant> findAllByMemberIdJoinFetchRecruitPost(Long memberId, Pageable pageable);
}
