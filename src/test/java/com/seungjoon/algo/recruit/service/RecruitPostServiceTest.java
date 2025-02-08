package com.seungjoon.algo.recruit.service;

import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.domain.MemberState;
import com.seungjoon.algo.member.domain.Role;
import com.seungjoon.algo.member.repository.MemberRepository;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.recruit.repository.ApplicantRepository;
import com.seungjoon.algo.recruit.repository.RecruitPostRepository;
import com.seungjoon.algo.study.domain.StudyRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
@MockitoSettings(strictness = Strictness.LENIENT)
class RecruitPostServiceTest {

    @InjectMocks
    private RecruitPostService recruitPostService;

    @Mock
    private RecruitPostRepository recruitPostRepository;
    @Mock
    private ApplicantRepository applicantRepository;
    @Mock
    private MemberRepository memberRepository;


    @Test
    void sameAuthorAndApplicant() {
        //given
        Member author = member(1L);
        RecruitPost post = recruitPost(1L, author);
        given(recruitPostRepository.findById(post.getId()))
                .willReturn(Optional.of(post));
        given(memberRepository.findById(author.getId()))
                .willReturn(Optional.of(author));

        given(recruitPostRepository.findById(post.getId()))
                .willReturn(Optional.of(post));

        given(memberRepository.findById(author.getId()))
                .willReturn(Optional.of(author));

        //when

        //then
        assertThatThrownBy(() -> recruitPostService.createApplicant(post.getId(), author.getId()))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(1013);
    }

    @Test
    void duplicateApplicant() {
        //given
        Member author = member(1L);
        RecruitPost post = recruitPost(1L, author);
        Member applicant = member(2L);

        given(recruitPostRepository.findById(post.getId()))
                .willReturn(Optional.of(post));
        given(memberRepository.findById(author.getId()))
                .willReturn(Optional.of(author));
        given(memberRepository.findById(applicant.getId()))
                .willReturn(Optional.of(applicant));
        given(applicantRepository.existsByRecruitPostIdAndMemberId(post.getId(), applicant.getId()))
                .willReturn(false)
                .willReturn(true);

        //when

        //then
        recruitPostService.createApplicant(post.getId(), applicant.getId());

        assertThatThrownBy(() -> recruitPostService.createApplicant(post.getId(), applicant.getId()))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(1012);
    }

    private Member member(Long id) {
        Member member = Member.builder()
                .email("test@test.com")
                .username("test")
                .role(Role.MEMBER)
                .state(MemberState.ACTIVE)
                .authType("normal")
                .build();

        ReflectionTestUtils.setField(member, "id", id);

        return member;
    }

    private RecruitPost recruitPost(Long id, Member member) {

        StudyRule studyRule = StudyRule.builder()
                .submitPerWeek(10)
                .totalWeek(5)
                .submitDayOfWeek(DayOfWeek.FRIDAY)
                .build();

        RecruitPost post = RecruitPost.builder()
                .title("title")
                .content("content")
                .member(member)
                .studyRule(studyRule)
                .build();

        ReflectionTestUtils.setField(post, "id", id);

        return post;
    }
}