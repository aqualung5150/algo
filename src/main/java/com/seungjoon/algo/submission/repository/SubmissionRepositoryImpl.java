package com.seungjoon.algo.submission.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seungjoon.algo.submission.domain.Submission;
import com.seungjoon.algo.submission.dto.SubmissionCondition;
import com.seungjoon.algo.utils.QuerydslUtils;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.seungjoon.algo.submission.domain.QSubmission.submission;

@Repository
public class SubmissionRepositoryImpl implements SubmissionRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public SubmissionRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Submission> findAllByCondition(SubmissionCondition condition, Pageable pageable) {

        List<Submission> submissions = queryFactory
                .selectFrom(submission)
                .join(submission.member).fetchJoin()
                .where(subjectNumber(condition.getSubjectNumber()))
                .orderBy(QuerydslUtils.getSort(pageable, submission))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(submission.count())
                .from(submission)
                .where(subjectNumber(condition.getSubjectNumber()));

        return PageableExecutionUtils.getPage(submissions, pageable, countQuery::fetchOne);
    }

    private BooleanExpression subjectNumber(Integer subjectNumber) {
        return subjectNumber != null ? submission.subjectNumber.eq(subjectNumber) : null;
    }
}
