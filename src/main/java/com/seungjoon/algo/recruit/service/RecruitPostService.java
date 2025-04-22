package com.seungjoon.algo.recruit.service;

import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.exception.UnauthorizedException;
import com.seungjoon.algo.image.domain.Image;
import com.seungjoon.algo.image.repository.ImageRepository;
import com.seungjoon.algo.image.util.ImageUtil;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.dto.ProfileResponse;
import com.seungjoon.algo.member.repository.MemberRepository;
import com.seungjoon.algo.recruit.domain.Applicant;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.recruit.domain.RecruitPostState;
import com.seungjoon.algo.recruit.dto.*;
import com.seungjoon.algo.recruit.repository.ApplicantRepository;
import com.seungjoon.algo.recruit.repository.RecruitPostRepository;
import com.seungjoon.algo.study.domain.StudyRule;
import com.seungjoon.algo.study.domain.StudyRuleTag;
import com.seungjoon.algo.study.repository.StudyRuleRepository;
import com.seungjoon.algo.submission.domain.Tag;
import com.seungjoon.algo.submission.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

import static com.seungjoon.algo.exception.ExceptionCode.*;
import static com.seungjoon.algo.image.domain.ImageType.RECRUIT_POST;
import static com.seungjoon.algo.image.domain.ImageType.TEMPORARY;
import static com.seungjoon.algo.recruit.domain.RecruitPostState.RECRUITING;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecruitPostService {

    private final RecruitPostRepository recruitPostRepository;
    private final ApplicantRepository applicantRepository;
    private final StudyRuleRepository studyRuleRepository;
    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;

    @Transactional
    public Long createRecruitPost(Long authId, CreateRecruitPostRequest request) {

        Member member = memberRepository.findById(authId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));

        List<Tag> tags = tagRepository.findByIdIn(request.getTags());
        validateTagsExist(request, tags);

        StudyRule studyRule = studyRuleRepository.save(StudyRule.builder()
                        .numberOfMembers(request.getNumberOfMembers())
                        .level(request.getLevel())
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

        // Update Image Type
        Set<String> imageIds = ImageUtil.extractImageIdsFromMarkdown(saved.getContent());
        List<Image> images = imageRepository.findAllById(imageIds);
        images.forEach(image -> {
            image.changeType(RECRUIT_POST);
            image.changeRecruitPost(saved);
        });

        return saved.getId();
    }

    @Transactional
    public void updateRecruitPost(Long postId, Long authId, CreateRecruitPostRequest request) {

        RecruitPost post = recruitPostRepository.findByIdJoinFetch(postId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_POST));

        if (!post.getMember().getId().equals(authId)) {
            throw new UnauthorizedException(NOT_OWN_RESOURCE);
        }

        if (post.getState() == RecruitPostState.COMPLETED) {
            throw new BadRequestException(RECRUITMENT_FINISHED);
        }

        List<Tag> tags = tagRepository.findByIdIn(request.getTags());
        validateTagsExist(request, tags);

        StudyRule studyRule = post.getStudyRule();
        studyRule.changeStudyRule(
                request.getNumberOfMembers(),
                request.getLevel(),
                request.getTotalWeek(),
                DayOfWeek.valueOf(request.getSubmitDayOfWeek()),
                request.getSubmitPerWeek()
        );

        List<StudyRuleTag> studyRuleTags = studyRule.getStudyRuleTags();
        StudyRuleTag.updateListFromTags(studyRule, studyRuleTags, tags);

        post.changeRecruitPost(request.getTitle(), request.getContent());

        //Update Images
        Set<String> curImages = ImageUtil.extractImageIdsFromMarkdown(post.getContent());
        List<Image> prevImages = imageRepository.findAllByRecruitPost(post);
        //Deleted Unused Images
        prevImages.stream().filter(image -> !curImages.contains(image.getId())).forEach(image -> {
            image.changeType(TEMPORARY);
            image.changeRecruitPost(null);
        });
        //Newly Added Images
        List<Image> images = imageRepository.findAllById(curImages);
        images.forEach(image -> {
            image.changeType(RECRUIT_POST);
            image.changeRecruitPost(post);
        });
    }

    @Transactional
    public void deleteRecruitPost(Long authId, Long postId) {

        RecruitPost post = recruitPostRepository.findByIdJoinFetch(postId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_POST));

        if (!post.getMember().getId().equals(authId)) {
            throw new UnauthorizedException(NOT_OWN_RESOURCE);
        }

        // Applicant 삭제
        /* 벌크 연산 - clearAutomatically, flushAutomatically 등 옵션이 필요할 수 있음 */
        applicantRepository.deleteAllByRecruitPostId(post.getId());

        // RecruitPost 삭제
        recruitPostRepository.deleteById(post.getId());

        // StudyRule 삭제
        //TODO: srt도 단건쿼리가 여러번 나감
        //-> @OnDelete로 해결 -> 적절한지 확인이 필요함
        if (post.getState() == RECRUITING) {
            studyRuleRepository.deleteById(post.getStudyRule().getId());
        }

        //Delete All Images
        Set<String> imageIds = ImageUtil.extractImageIdsFromMarkdown(post.getContent());
        List<Image> images = imageRepository.findAllById(imageIds);
        images.forEach(image -> {
            image.changeType(TEMPORARY);
            image.changeRecruitPost(null);
        });
    }

    @Transactional
    public Long createApplicant(Long postId, Long authId) {

        RecruitPost post = recruitPostRepository.findById(postId).orElseThrow(() -> new BadRequestException(NOT_FOUND_POST));
        Member member = memberRepository.findById(authId).orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));

        validateApplicant(post, member);

        Applicant saved = applicantRepository.save(
                Applicant.builder()
                        .recruitPost(post)
                        .member(member)
                        .build()
        );

        return saved.getId();
    }

    @Transactional
    public void deleteApplicant(Long postId, Long memberId) {

//        Applicant applicant = applicantRepository.findByRecruitPostIdAndMemberId(postId, id)
//                .orElseThrow(() -> new BadRequestException(NOT_FOUND_APPLICANT));

//        applicantRepository.delete();

        applicantRepository.deleteByRecruitPostIdAndMemberId(postId, memberId);
    }

    public RecruitPostPageResponse getRecruitPostList(
            RecruitPostCondition condition,
            Pageable pageable
    ) {

        Page<RecruitPost> posts = recruitPostRepository.findAllByCondition(condition, pageable);

        long totalCount = posts.getTotalElements();

        return RecruitPostPageResponse.of(
                totalCount,
                posts.getContent()
        );
    }

    public RecruitPostResponse getRecruitPostById(Long id) {
        return RecruitPostResponse.from(recruitPostRepository.findByIdJoinFetch(id)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_POST)));
    }

    public ApplicantProfileSliceResponse getApplications(Long postId, Pageable pageable) {
        Slice<Applicant> slice = applicantRepository.findAllByPostIdJoinFetchMember(postId, pageable);

        return ApplicantProfileSliceResponse.of(
                slice.hasNext(),
                slice.stream()
                        .map(applicant -> ProfileResponse.from(applicant.getMember()))
                        .toList()
        );
    }

    public void existsByRecruitPostIdAndMemberId(Long postId, Long memberId) {

        if (!applicantRepository.existsByRecruitPostIdAndMemberId(postId, memberId)) {
            throw new BadRequestException(NOT_FOUND_APPLICANT);
        }
    }

    public RecruitPostSliceResponse getByApplicantMemberId(Long memberId, Pageable pageable) {

        Slice<RecruitPost> posts = recruitPostRepository.findByApplicantMemberId(memberId, pageable);

        return RecruitPostSliceResponse.of(
                posts.hasNext(),
                posts.getContent()
        );
    }

    public RecruitPostSliceResponse getByMemberId(Long memberId, Pageable pageable) {
        Slice<RecruitPost> posts = recruitPostRepository.findByMemberIdJoinFetch(memberId, pageable);

        return RecruitPostSliceResponse.of(
                posts.hasNext(),
                posts.getContent()
        );
    }

    private void validateApplicant(RecruitPost post, Member member) {

        if (post.getState() == RecruitPostState.COMPLETED) {
            throw new BadRequestException(RECRUITMENT_FINISHED);
        }

        if (post.getMember().getId().equals(member.getId())) {
            throw new BadRequestException(SAME_AUTHOR_APPLICANT);
        }

        if (applicantRepository.existsByRecruitPostIdAndMemberId(post.getId(), member.getId())) {
            throw new BadRequestException(DUPLICATE_APPLICANT);
        }
    }

    private void validateTagsExist(CreateRecruitPostRequest request, List<Tag> tags) {
        if (tags.size() != request.getTags().size()) {
            throw new BadRequestException(INVALID_TAGS);
        }
    }
}
