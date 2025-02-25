package com.seungjoon.algo.recruit.repository;

import com.seungjoon.algo.recruit.domain.Applicant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

    boolean existsByRecruitPostIdAndMemberId(Long postId, Long memberId);

    void deleteByRecruitPostIdAndMemberId(Long postId, Long memberId);

    @Query(value = "select a from Applicant a join fetch a.member where a.recruitPost.id = :postId")
    List<Applicant> findAllByPostIdJoinFetchMember(Long postId);

    @Query(value = "select a from Applicant a join fetch a.member where a.recruitPost.id = :postId")
    Slice<Applicant> findAllByPostIdJoinFetchMember(Long postId, Pageable pageable);

    @Query(value = "select a from Applicant a join fetch a.recruitPost where a.member.id = :memberId")
    Slice<Applicant> findAllByMemberIdJoinFetchRecruitPost(Long memberId, Pageable pageable);

    @Modifying
    @Query("delete from Applicant a where a.recruitPost.id = :postId")
    void deleteAllByRecruitPostId(Long postId);
}
