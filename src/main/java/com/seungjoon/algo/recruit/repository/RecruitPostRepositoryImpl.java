package com.seungjoon.algo.recruit.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.recruit.domain.RecruitPostState;
import com.seungjoon.algo.recruit.dto.RecruitPostCondition;
import com.seungjoon.algo.utils.QuerydslUtils;
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
import static com.seungjoon.algo.submission.domain.QTag.tag;

@Repository
public class RecruitPostRepositoryImpl implements RecruitPostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public RecruitPostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /*TODO: 역정규화
           또는
           (X)GROUP_CONCAT(https://thisiswoo.github.io/development/using-jpa-querydsl-groupconcat-func.html)
           -> RecruitPost를 객체지향적으로 받고 싶기 때문에 적절하지 않을 수 있음
           또는
           StudyRuleTag에 대해 조회?
         */
    @Override
    public Page<RecruitPost> findAllByCondition(RecruitPostCondition condition, Pageable pageable) {

        List<RecruitPost> posts = queryFactory
                .selectFrom(recruitPost)
                .join(recruitPost.studyRule, studyRule).fetchJoin()
                .join(recruitPost.member).fetchJoin()
                .where(
                        studyRuleInByTag(condition.getTag()),
                        state(condition.getState()),
                        level(condition.getLevel()),
                        titleContains(condition.getTitle())
                )
                .orderBy(QuerydslUtils.getSort(pageable, recruitPost))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(recruitPost.countDistinct())
                .from(recruitPost);

        joinStudyRuleByCondition(countQuery, condition)
                .where(
                        studyRuleInByTag(condition.getTag()),
                        state(condition.getState()),
                        level(condition.getLevel()),
                        titleContains(condition.getTitle())
                );

        return PageableExecutionUtils.getPage(posts, pageable, countQuery::fetchOne);
    }

    private BooleanExpression studyRuleInByTag(List<Long> tagIds) {

        if (ObjectUtils.isEmpty(tagIds)) {
            return null;
        }

        JPAQuery<Long> studyRuleIdsByTagQuery = queryFactory
                .select(studyRuleTag.studyRule.id)
                .from(studyRuleTag)
                .join(studyRuleTag.tag, tag)
                .where(tag.id.in(tagIds))
                .distinct();

        return studyRule.id.in(studyRuleIdsByTagQuery);
    }

    private BooleanExpression level(Integer level) {
        return level != null ? studyRule.level.eq(level) : null;
    }

    private BooleanExpression state(String state) {
        return StringUtils.hasText(state) ? recruitPost.state.eq(RecruitPostState.valueOf(state)) : null;
    }

    private BooleanExpression titleContains(String title) {
        return StringUtils.hasText(title) ? recruitPost.title.containsIgnoreCase(title) : null;
    }

    private JPAQuery<Long> joinStudyRuleByCondition(
            JPAQuery<Long> countQuery,
            RecruitPostCondition condition
    ) {
        if (
                condition.getLevel() != null ||
                StringUtils.hasText(condition.getState()) ||
                !ObjectUtils.isEmpty(condition.getTag())
        ) {
            countQuery = countQuery.join(recruitPost.studyRule);
        }

        return countQuery;
    }
}
