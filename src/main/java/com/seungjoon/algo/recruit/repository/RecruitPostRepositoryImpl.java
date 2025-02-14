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

    /*TODO: 역정규화
       또는
       (X)GROUP_CONCAT(https://thisiswoo.github.io/development/using-jpa-querydsl-groupconcat-func.html)
       -> RecruitPost를 객체지향적으로 받고 싶기 때문에 적절하지 않을 수 있음
       또는
       StudyRuleTag에 대해 조회?
     */
    @Override
    public Page<RecruitPost> findAllJoinFetch(RecruitPostSearchCondition condition, Pageable pageable) {

        //TODO: join 동적으로 하기
        List<Long> studyRuleIds = queryFactory
                .select(studyRuleTag.studyRule.id)
                .from(studyRuleTag)
                .join(studyRuleTag.tag, tag)
                .where(tagIn(condition.getTag()))
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<RecruitPost> posts = queryFactory
                .selectFrom(recruitPost)
                .join(recruitPost.studyRule, studyRule).fetchJoin()
                .join(recruitPost.member).fetchJoin()
                .where(
                        studyRule.id.in(studyRuleIds),
                        titleContainsIgnoreCase(condition.getTitle())
                )
                .orderBy(recruitPost.id.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(studyRuleTag.studyRule.countDistinct())
                .from(studyRuleTag)
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
