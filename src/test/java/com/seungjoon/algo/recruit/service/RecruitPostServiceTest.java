package com.seungjoon.algo.recruit.service;

import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.domain.MemberState;
import com.seungjoon.algo.member.domain.Role;
import com.seungjoon.algo.member.repository.MemberRepository;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.recruit.dto.RecruitPostListResponse;
import com.seungjoon.algo.recruit.dto.RecruitPostResponse;
import com.seungjoon.algo.recruit.repository.RecruitPostRepository;
import com.seungjoon.algo.study.domain.StudyRule;
import com.seungjoon.algo.study.repository.StudyRuleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RecruitPostServiceTest {

    @Autowired
    private RecruitPostService recruitPostService;
    @Autowired
    private RecruitPostRepository recruitPostRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StudyRuleRepository studyRuleRepository;

    @Test
    void list() {

        //given - RecruitPost 30개
        saveList();
        //when
        RecruitPostListResponse page1 = recruitPostService.getList(0, 20);
        RecruitPostListResponse page2 = recruitPostService.getList(1, 20);

        //then
        List<RecruitPostResponse> posts1 = page1.getPosts();
        assertThat(page1.getTotalElements()).isEqualTo(30L);
        assertThat(posts1.size()).isEqualTo(20);

        List<RecruitPostResponse> posts2 = page2.getPosts();
        assertThat(page2.getTotalElements()).isEqualTo(30L);
        assertThat(posts2.size()).isEqualTo(10);

        assertThat(posts1.get(0).getTitle()).isEqualTo("title1");
        assertThat(posts2.get(0).getTitle()).isEqualTo("title21");
        assertThat(posts2.get(posts2.size() - 1).getTitle()).isEqualTo("title30");
    }

    private void saveList() {
        Member member = Member.builder()
                .email("test@test.com")
                .username("test")
                .role(Role.MEMBER)
                .state(MemberState.ACTIVE)
                .authType("normal")
                .build();
        memberRepository.save(member);

        List<StudyRule> studyRules = new ArrayList<>();
        for (int i = 1; i <= 30; ++i) {
            studyRules.add(
                    StudyRule.builder()
                            .submitPerWeek(10)
                            .totalWeek(5)
                            .submitDayOfWeek(DayOfWeek.FRIDAY)
                            .build()
            );
        }
        studyRuleRepository.saveAll(studyRules);

        List<RecruitPost> recruitPosts = new ArrayList<>();
        for (int i = 1; i <= 30; ++i) {
            StudyRule studyRule = studyRules.get(i - 1);
            recruitPosts.add(
                    RecruitPost.builder()
                            .title("title" + i)
                            .content("content" + i)
                            .member(member)
                            .studyRule(studyRule)
                            .build()
            );
        }
        recruitPostRepository.saveAll(recruitPosts);
    }
}