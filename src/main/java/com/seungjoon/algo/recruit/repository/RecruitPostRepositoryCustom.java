package com.seungjoon.algo.recruit.repository;

import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.recruit.dto.RecruitPostSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecruitPostRepositoryCustom {
    Page<RecruitPost> findAllJoinFetch(RecruitPostSearchCondition condition, Pageable pageable);
}
