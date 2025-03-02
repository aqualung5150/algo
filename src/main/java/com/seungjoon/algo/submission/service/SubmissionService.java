package com.seungjoon.algo.submission.service;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.exception.UnauthorizedException;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.repository.MemberRepository;
import com.seungjoon.algo.study.domain.Study;
import com.seungjoon.algo.study.domain.StudyMember;
import com.seungjoon.algo.submission.dto.CreateSubmissionRequest;
import com.seungjoon.algo.study.repository.StudyRepository;
import com.seungjoon.algo.submission.domain.Submission;
import com.seungjoon.algo.submission.domain.SubmissionTag;
import com.seungjoon.algo.submission.domain.SubmissionVisibility;
import com.seungjoon.algo.submission.domain.Tag;
import com.seungjoon.algo.submission.dto.SubmissionCondition;
import com.seungjoon.algo.submission.dto.SubmissionPageResponse;
import com.seungjoon.algo.submission.dto.SubmissionResponse;
import com.seungjoon.algo.submission.repository.SubmissionRepository;
import com.seungjoon.algo.submission.repository.SubmissionTagRepository;
import com.seungjoon.algo.submission.repository.TagRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static com.seungjoon.algo.exception.ExceptionCode.*;
import static com.seungjoon.algo.exception.ExceptionCode.NOT_FOUND_MEMBER;
import static com.seungjoon.algo.study.domain.StudyState.IN_PROGRESS;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionTagRepository submissionTagRepository;
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;

    public SubmissionResponse getSubmissionById(PrincipalDetails auth, Long id) {

        Submission submission = submissionRepository.findByIdJoinFetchMember(id).orElseThrow(() -> new BadRequestException(NOT_FOUND_POST));

        if (submission.getVisibility() == SubmissionVisibility.PRIVATE) {
            validateAccessible(auth, submission);
        }

        return SubmissionResponse.from(submission);
    }

    private void validateAccessible(PrincipalDetails auth, Submission submission) {

        if (auth == null) {
            throw new UnauthorizedException(PRIVATE_POST);
        }

        List<Long> members = submission.getStudy()
                .getStudyMembers().stream()
                .mapToLong(StudyMember::getId).boxed()
                .toList();

        if (!members.contains(submission.getId())) {
            throw new UnauthorizedException(PRIVATE_POST);
        }
    }

    public SubmissionPageResponse getSubmissions(SubmissionCondition condition, Pageable pageable) {

        Page<Submission> page = submissionRepository.findAllByCondition(condition, pageable);
        return SubmissionPageResponse.of(page.getTotalElements(), page.getContent());
    }

    @Transactional
    public Long submit(Long memberId, @Valid CreateSubmissionRequest request) {

        Study study = studyRepository.findByIdJoinFetch(request.getStudyId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_STUDY));

        validateStudyInProgress(study);
        validateMemberInStudy(study, memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));

        List<Tag> tags = tagRepository.findByIdIn(request.getTags());
        validateTagsExist(request, tags);

        Submission submission = submissionRepository.save(Submission.builder()
                .study(study)
                .member(member)
                .content(request.getContent())
                .subjectNumber(request.getSubjectNumber())
                .visibility(SubmissionVisibility.valueOf(request.getVisibility()))
                .weekNumber(getCurrentWeek(study))
                .build());

        List<SubmissionTag> submissionTags = SubmissionTag.toListFromTags(submission, tags);
        submissionTagRepository.saveAll(submissionTags);

        //TODO: oneToMany를 배치인서트할 지 고민이 된다...
        submission.addSubmissionTags(submissionTags);

        return submission.getId();
    }

    private void validateStudyInProgress(Study study) {
        if (study.getState() != IN_PROGRESS) {
            throw new BadRequestException(STUDY_CLOSED);
        }
    }

    private void validateMemberInStudy(Study study, Long memberId) {

        List<Long> memberIds = study.getStudyMembers().stream()
                .mapToLong(studyMember -> studyMember.getMember().getId())
                .boxed().toList();

        if (!memberIds.contains(memberId)) {
            throw new BadRequestException(MEMBER_NOT_IN_STUDY);
        }
    }

    private void validateTagsExist(CreateSubmissionRequest request, List<Tag> tags) {
        if (tags.size() != request.getTags().size()) {
            throw new BadRequestException(INVALID_TAGS);
        }
    }

    private Integer getCurrentWeek(Study study) {
        int weekNumber = 1;
        LocalDate startSubmitDate = study.getFirstSubmitDate();
        LocalDate nextSubmitDate = LocalDate.now()
                .with(TemporalAdjusters.nextOrSame(study.getStudyRule().getSubmitDayOfWeek()));
        while (nextSubmitDate.isAfter(startSubmitDate)) {
            startSubmitDate = startSubmitDate.plusWeeks(1L);
            ++weekNumber;
        }
        return weekNumber;
    }
}
