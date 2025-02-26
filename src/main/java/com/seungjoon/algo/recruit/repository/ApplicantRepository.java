package com.seungjoon.algo.recruit.repository;

import com.seungjoon.algo.recruit.domain.Applicant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

//TODO: findByXXXId 불필요한 join 발생하는지 확인해보고 리팩토링하기
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    //TODO: limit 1
    boolean existsByRecruitPostIdAndMemberId(Long postId, Long memberId);

    @Modifying
    @Query("delete from Applicant a where a.recruitPost.id = :postId and a.member.id = :memberId")
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
