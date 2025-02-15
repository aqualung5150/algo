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

        List<RecruitPost> posts = queryFactory
                .selectFrom(recruitPost)
                .join(recruitPost.member).fetchJoin()
                .join(recruitPost.studyRule).fetchJoin()
                .where(titleContainsIgnoreCase(condition.getTitle()))
                .orderBy(recruitPost.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(recruitPost.count())
                .from(recruitPost)
                .where(titleContainsIgnoreCase(condition.getTitle()));

        return PageableExecutionUtils.getPage(posts, pageable, countQuery::fetchOne);
    }

    /*TODO: 역정규화
           또는
           (X)GROUP_CONCAT(https://thisiswoo.github.io/development/using-jpa-querydsl-groupconcat-func.html)
           -> RecruitPost를 객체지향적으로 받고 싶기 때문에 적절하지 않을 수 있음
           또는
           StudyRuleTag에 대해 조회?
         */
    @Override
    public Page<RecruitPost> findAllByTag(RecruitPostSearchCondition condition, Pageable pageable) {

        JPAQuery<Long> studyRuleIdsQuery = queryFactory
                .select(studyRuleTag.studyRule.id)
                .from(studyRuleTag)
                .join(studyRuleTag.tag, tag)
                .where(tagIn(condition.getTag()))
                .distinct();

        // where문 안에서 서브쿼리 성능 테스트
        List<RecruitPost> posts = queryFactory
                .selectFrom(recruitPost)
                .join(recruitPost.studyRule, studyRule).fetchJoin()
                .join(recruitPost.member).fetchJoin()
                .where(
                        studyRule.id.in(studyRuleIdsQuery),
                        titleContainsIgnoreCase(condition.getTitle())
                )
                .orderBy(recruitPost.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(recruitPost.countDistinct())
                .from(recruitPost)
                .join(recruitPost.studyRule, studyRule)
                .where(
                        studyRule.id.in(studyRuleIdsQuery),
                        titleContainsIgnoreCase(condition.getTitle())
                );

        return PageableExecutionUtils.getPage(posts, pageable, countQuery::fetchOne);
    }

    private BooleanExpression tagIn(List<Long> tagIds) {
        return !ObjectUtils.isEmpty(tagIds) ? tag.id.in(tagIds) : null;
    }

    private BooleanExpression titleContainsIgnoreCase(String title) {
        return StringUtils.hasText(title) ? recruitPost.title.containsIgnoreCase(title) : null;
    }
}
