package com.seungjoon.algo.recruit.repository;

import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.repository.MemberRepository;
import com.seungjoon.algo.recruit.domain.Applicant;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.recruit.dto.RecruitPostSearchCondition;
import com.seungjoon.algo.study.domain.StudyRule;
import com.seungjoon.algo.study.domain.StudyRuleTag;
import com.seungjoon.algo.study.repository.StudyRuleRepository;
import com.seungjoon.algo.study.repository.StudyRuleTagRepository;
import com.seungjoon.algo.subject.domain.Tag;
import com.seungjoon.algo.subject.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.time.DayOfWeek;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class RecruitPostRepositoryTest {

    @Autowired
    RecruitPostRepository recruitPostRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    StudyRuleRepository studyRuleRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    private StudyRuleTagRepository studyRuleTagRepository;
    @Autowired
    private ApplicantRepository applicantRepository;

    @BeforeEach
    void init() {
        /* Tag */
        tagRepository.save(new Tag(1L, "dp"));
        tagRepository.save(new Tag(2L, "dfs"));
        tagRepository.save(new Tag(3L, "bfs"));
        /* Member */
        Member member = memberRepository.save(Member.builder()
                .username("user1")
                .email("user1@email.com")
                .build());
        /* StudyRule, RecruitPost */
        for (long i = 1; i <= 3; ++i) {
            StudyRule studyRule = StudyRule.builder()
                    .minLevel(5 * (int)i)
                    .maxLevel(5 * (int)i + 10)
                    .totalWeek(10)
                    .submitPerWeek(3)
                    .submitDayOfWeek(DayOfWeek.FRIDAY)
                    .build();

            List<StudyRuleTag> studyRuleTags = StudyRuleTag.toListFromTags(
                    studyRule,
                    tagRepository.findByIdIn(List.of(i, i % 3L + 1))
            );

            studyRule.addStudyRuleTags(studyRuleTags);

            StudyRule savedStudyRule = studyRuleRepository.save(studyRule);

            recruitPostRepository.save(RecruitPost.builder()
                    .studyRule(savedStudyRule)
                    .title("post" + i)
                    .member(member)
                    .build());
        }
    }

    @Test
    @DisplayName("페이징")
    void findAllPaging() {
        //given
        PageRequest pageRequest = PageRequest.of(
                0,
                2,
                Sort.Direction.DESC,
                "createdDate"
        );

        RecruitPostSearchCondition condition = new RecruitPostSearchCondition();

        //when
        Page<RecruitPost> posts = recruitPostRepository
                .findAllByCondition(condition, pageRequest);

        //then
        assertThat(posts.getTotalElements()).isEqualTo(3);
        assertThat(posts.getContent().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Tag 조건 필터링")
    void findAllByTags() {
        //given
        PageRequest pageRequest = PageRequest.of(
                0,
                10,
                Sort.Direction.DESC,
                "createdDate"
        );

        RecruitPostSearchCondition condition = new RecruitPostSearchCondition();
        condition.setTag(List.of(1L));

        //when
        Page<RecruitPost> posts = recruitPostRepository
                .findAllByCondition(condition, pageRequest);

        //then
        assertThat(posts.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Level 조건 필터링")
    void findAllByLevel() {
        //given
        PageRequest pageRequest = PageRequest.of(
                0,
                10,
                Sort.Direction.DESC,
                "createdDate"
        );

        //when
        RecruitPostSearchCondition minLevel = new RecruitPostSearchCondition();
        minLevel.setMinLevel(15);
        Page<RecruitPost> minLevelPosts = recruitPostRepository
                .findAllByCondition(minLevel, pageRequest);

        RecruitPostSearchCondition maxLevel = new RecruitPostSearchCondition();
        maxLevel.setMaxLevel(15);
        Page<RecruitPost> maxLevelPosts = recruitPostRepository
                .findAllByCondition(maxLevel, pageRequest);

        RecruitPostSearchCondition minMaxLevel = new RecruitPostSearchCondition();
        minMaxLevel.setMinLevel(15);
        minMaxLevel.setMaxLevel(25);
        Page<RecruitPost> minMaxLevelPosts = recruitPostRepository
                .findAllByCondition(minMaxLevel, pageRequest);

        //then
        assertThat(minLevelPosts.getTotalElements()).isEqualTo(1);
        assertThat(maxLevelPosts.getTotalElements()).isEqualTo(1);
        assertThat(minMaxLevelPosts.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("title 조건 필터링")
    void findAllByTitle() {
        //given
        PageRequest pageRequest = PageRequest.of(
                0,
                10,
                Sort.Direction.DESC,
                "createdDate"
        );

        RecruitPostSearchCondition condition = new RecruitPostSearchCondition();
        condition.setTitle("post3");

        //when
        Page<RecruitPost> posts = recruitPostRepository
                .findAllByCondition(condition, pageRequest);

        //then
        assertThat(posts.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("모든 조건 필터링")
    void findAllByAllCondition() {
        //given
        PageRequest pageRequest = PageRequest.of(
                0,
                10,
                Sort.Direction.DESC,
                "createdDate"
        );

        RecruitPostSearchCondition condition = new RecruitPostSearchCondition();
        condition.setTag(List.of(1L, 3L));
        condition.setTitle("post3");
        condition.setMinLevel(15);
        condition.setMaxLevel(25);

        //when
        Page<RecruitPost> posts = recruitPostRepository
                .findAllByCondition(condition, pageRequest);

        //then
        assertThat(posts.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("모든 조건 + 페이징")
    void findAllByAllConditionPaging() {
        //given
        PageRequest pageRequest = PageRequest.of(
                0,
                1,
                Sort.Direction.DESC,
                "createdDate"
        );

        RecruitPostSearchCondition condition = new RecruitPostSearchCondition();
        condition.setTag(List.of(1L, 3L));
        condition.setTitle("post");
        condition.setMinLevel(5);
        condition.setMaxLevel(25);

        //when
        Page<RecruitPost> posts = recruitPostRepository
                .findAllByCondition(condition, pageRequest);

        //then
        assertThat(posts.getTotalElements()).isEqualTo(3);
        assertThat(posts.getContent().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("지원자의 id로 지원한 모집글 조회")
    void findAllByApplicantId() {
        //given
        RecruitPost post = recruitPostRepository.save(RecruitPost.builder()
                .title("post")
                .build());
        Member member = memberRepository.save(Member.builder()
                .username("member")
                .email("member@email.com")
                .build());

        Applicant applicant = applicantRepository.save(Applicant.builder()
                .recruitPost(post)
                .member(member)
                .build()
        );

        //when
        Slice<RecruitPost> result = recruitPostRepository.findByApplicantMemberId(member.getId(), PageRequest.of(0, 10));

        //then
        assertThat(result.hasNext()).isFalse();
        assertThat(result.getContent().size()).isEqualTo(1);
    }
}