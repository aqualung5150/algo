package com.seungjoon.algo.recruit.repository;

import com.seungjoon.algo.recruit.domain.RecruitPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RecruitPostRepository extends JpaRepository<RecruitPost, Long> {

    @Query("select r from RecruitPost r join fetch r.member where r.id = :id")
    Optional<RecruitPost> findByIdJoinFetchMember(Long id);
}
