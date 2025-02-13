package com.seungjoon.algo.recruit.repository;

import com.seungjoon.algo.recruit.domain.RecruitPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RecruitPostRepository extends JpaRepository<RecruitPost, Long>, RecruitPostRepositoryCustom {

    @Query("select r from RecruitPost r" +
            " join fetch r.member" +
            " join fetch r.studyRule sr" +
            " join fetch sr.studyRuleTags srt")
    Page<RecruitPost> findAllJoinFetch(Pageable pageable);

    @Query("select distinct r from RecruitPost r" +
            " join fetch r.member" +
            " join fetch r.studyRule sr" +
            " where r.id = :id")
    Optional<RecruitPost> findByIdJoinFetch(Long id);
}
