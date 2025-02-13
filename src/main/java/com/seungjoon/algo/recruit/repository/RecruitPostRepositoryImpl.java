package com.seungjoon.algo.recruit.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seungjoon.algo.member.domain.QMember;
import com.seungjoon.algo.recruit.domain.QRecruitPost;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.seungjoon.algo.member.domain.QMember.*;
import static com.seungjoon.algo.recruit.domain.QRecruitPost.*;

@Repository
public class RecruitPostRepositoryImpl implements RecruitPostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public RecruitPostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<RecruitPost> findCustomById(Long id) {
        return Optional.ofNullable(queryFactory
                .selectFrom(recruitPost)
                .where(recruitPost.id.eq(id))
                .fetchOne());
    }
}
