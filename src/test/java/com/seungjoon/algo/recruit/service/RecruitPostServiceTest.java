package com.seungjoon.algo.recruit.service;

import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.repository.MemberRepository;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.recruit.repository.ApplicantRepository;
import com.seungjoon.algo.recruit.repository.RecruitPostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

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
        Member author = mock(Member.class);
        given(author.getId()).willReturn(1L);

        RecruitPost post = mock(RecruitPost.class);
        given(post.getId()).willReturn(1L);
        given(post.getMember()).willReturn(author);

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
        Member author = mock(Member.class);
        given(author.getId()).willReturn(1L);

        RecruitPost post = mock(RecruitPost.class);
        given(post.getId()).willReturn(1L);
        given(post.getMember()).willReturn(author);

        Member applicant = mock(Member.class);
        given(applicant.getId()).willReturn(2L);

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
}