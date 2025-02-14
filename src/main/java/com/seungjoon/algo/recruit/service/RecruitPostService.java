package com.seungjoon.algo.recruit.service;

import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.dto.ProfileResponse;
import com.seungjoon.algo.member.repository.MemberRepository;
import com.seungjoon.algo.recruit.domain.Applicant;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.recruit.dto.*;
import com.seungjoon.algo.recruit.repository.ApplicantRepository;
import com.seungjoon.algo.recruit.repository.RecruitPostRepository;
import com.seungjoon.algo.study.domain.StudyRule;
import com.seungjoon.algo.study.domain.StudyRuleTag;
import com.seungjoon.algo.study.repository.StudyRuleRepository;
import com.seungjoon.algo.study.repository.StudyRuleTagRepository;
import com.seungjoon.algo.subject.domain.Tag;
import com.seungjoon.algo.subject.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;

import static com.seungjoon.algo.exception.ExceptionCode.*;
import static com.seungjoon.algo.exception.ExceptionCode.NOT_FOUND_MEMBER;
import static com.seungjoon.algo.exception.ExceptionCode.NOT_FOUND_POST;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecruitPostService {

    private final RecruitPostRepository recruitPostRepository;
    private final ApplicantRepository applicantRepository;
    private final StudyRuleRepository studyRuleRepository;
    private final TagRepository tagRepository;
    private final StudyRuleTagRepository studyRuleTagRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long createRecruitPost(Long memberId, CreateRecruitPostRequest request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));

        List<Tag> tags = tagRepository.findByIdIn(request.getTags());

        if (tags.size() != request.getTags().size()) {
            throw new BadRequestException(INVALID_TAGS);
        }

        StudyRule studyRule = studyRuleRepository.save(StudyRule.builder()
                        .submitDayOfWeek(DayOfWeek.valueOf(request.getSubmitDayOfWeek()))
                        .totalWeek(request.getTotalWeek())
                        .submitPerWeek(request.getSubmitPerWeek())
                        .build()
        );

        List<StudyRuleTag> studyRuleTags = StudyRuleTag.toListFromTags(studyRule, tags);
        studyRule.addStudyRuleTags(studyRuleTags);

        RecruitPost saved = recruitPostRepository.save(RecruitPost.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .studyRule(studyRule)
                .member(member)
                .build()
        );

        return saved.getId();
    }

    @Transactional
    public Applicant createApplicant(Long postId, Long memberId) {

        RecruitPost post = recruitPostRepository.findById(postId).orElseThrow(() -> new BadRequestException(NOT_FOUND_POST));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));

        validateApplicant(post, member);

        return applicantRepository.save(
                Applicant.builder()
                        .recruitPost(post)
                        .member(member)
                        .build()
        );
    }

    private void validateApplicant(RecruitPost post, Member member) {

        if (post.getMember().getId().equals(member.getId())) {
            throw new BadRequestException(SAME_AUTHOR_APPLICANT);
        }

        if (applicantRepository.existsByRecruitPostIdAndMemberId(post.getId(), member.getId())) {
            throw new BadRequestException(DUPLICATE_APPLICANT);
        }
    }

    public RecruitPostPageResponse getRecruitPostList(RecruitPostSearchCondition condition, Pageable pageable) {
        //TODO: 블로그 - N + 1해결
        Page<RecruitPost> list = recruitPostRepository.findAllJoinFetch(condition, pageable);
//        Page<RecruitPost> list = recruitPostRepository.findAllJoinFetch(pageable);
//        Page<RecruitPost> list = recruitPostRepository.findAll(pageable);
        long totalCount = list.getTotalElements();

        return RecruitPostPageResponse.of(
                totalCount,
                list.getContent().stream()
                        .map(RecruitPostResponse::from)
                        .toList()
        );
    }

    public RecruitPost getRecruitPostById(Long id) {
        //TODO: N + 1 문제 해결 - RecruitPost의 Member, StudyRule, StudyRuleTag, Tag를 어떻게 가져올 것인가.
        return recruitPostRepository.findByIdJoinFetch(id).orElseThrow(() -> new BadRequestException(NOT_FOUND_POST));
//        return recruitPostRepository.findById(id).orElseThrow(() -> new BadRequestException(NOT_FOUND_POST));
    }

    public ApplicantProfileSliceResponse getApplicantProfileListByPostId(Long id, Pageable pageable) {
        Slice<Applicant> slice = applicantRepository.findAllByPostIdJoinFetchMember(id, pageable);

        List<ProfileResponse> applicants = slice.stream()
                .map(applicant -> ProfileResponse.from(applicant.getMember()))
                .toList();

        return ApplicantProfileSliceResponse.of(slice.hasNext(), applicants);
    }

    public void existsByRecruitPostIdAndMemberId(Long postId, Long memberId) {

        if (!applicantRepository.existsByRecruitPostIdAndMemberId(postId, memberId)) {
            throw new BadRequestException(NOT_FOUND_APPLICANT);
        }
    }
}
