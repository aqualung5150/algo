package com.seungjoon.algo.recruit.repository;

import com.seungjoon.algo.recruit.domain.RecruitPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitPostRepository extends JpaRepository<RecruitPost, Long> {
}
