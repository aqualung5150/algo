package com.seungjoon.algo.recruit.repository;

import com.seungjoon.algo.recruit.domain.RecruitPost;

import java.util.Optional;

public interface RecruitPostRepositoryCustom {

    Optional<RecruitPost> findCustomById(Long id);
}
