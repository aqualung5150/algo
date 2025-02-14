package com.seungjoon.algo.recruit.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.recruit.dto.RecruitPostSearchCondition;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.seungjoon.algo.recruit.domain.QRecruitPost.recruitPost;
import static com.seungjoon.algo.study.domain.QStudyRule.studyRule;
import static com.seungjoon.algo.study.domain.QStudyRuleTag.studyRuleTag;
import static com.seungjoon.algo.subject.domain.QTag.tag;

@Repository
public class RecruitPostRepositoryImpl implements RecruitPostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public RecruitPostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<RecruitPost> findAllJoinFetch(RecruitPostSearchCondition condition, Pageable pageable) {

        JPAQuery<Long> orderIdQuery = queryFactory
                .select(recruitPost.id)
                .from(recruitPost)
                .join(recruitPost.studyRule, studyRule)
                .join(studyRule.studyRuleTags, studyRuleTag)
                .join(studyRuleTag.tag, tag)
                .where(tagIn(condition.getTag()))
                .distinct();

        List<RecruitPost> posts = queryFactory
                .selectFrom(recruitPost)
                .join(recruitPost.studyRule).fetchJoin()
                .join(recruitPost.member).fetchJoin()
                .where(recruitPost.id.in(orderIdQuery
                                .offset(pageable.getOffset())
                                .limit(pageable.getPageSize())),
                        titleContainsIgnoreCase(condition.getTitle()))
                .orderBy(recruitPost.id.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(recruitPost.countDistinct())
                .from(recruitPost)
                .join(recruitPost.studyRule, studyRule)
                .join(studyRule.studyRuleTags, studyRuleTag)
                .join(studyRuleTag.tag, tag)
                .where(tagIn(condition.getTag()));

        return PageableExecutionUtils.getPage(posts, pageable, countQuery::fetchOne);
    }

    private BooleanExpression tagIn(List<Long> tagIds) {
        return !ObjectUtils.isEmpty(tagIds) ? tag.id.in(tagIds) : null;
    }

    private BooleanExpression titleContainsIgnoreCase(String title) {
        return StringUtils.hasText(title) ? recruitPost.title.containsIgnoreCase(title) : null;
    }
}
