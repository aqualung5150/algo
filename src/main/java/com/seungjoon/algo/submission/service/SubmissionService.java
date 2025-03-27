package com.seungjoon.algo.submission.service;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.exception.UnauthorizedException;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.repository.MemberRepository;
import com.seungjoon.algo.study.domain.Study;
import com.seungjoon.algo.study.domain.StudyMember;
import com.seungjoon.algo.study.repository.StudyRepository;
import com.seungjoon.algo.submission.domain.*;
import com.seungjoon.algo.submission.dto.*;
import com.seungjoon.algo.submission.repository.EvaluationRepository;
import com.seungjoon.algo.submission.repository.SubmissionRepository;
import com.seungjoon.algo.submission.repository.SubmissionTagRepository;
import com.seungjoon.algo.submission.repository.TagRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import static com.seungjoon.algo.exception.ExceptionCode.*;
import static com.seungjoon.algo.study.domain.StudyMemberState.BANNED;
import static com.seungjoon.algo.study.domain.StudyState.IN_PROGRESS;
import static com.seungjoon.algo.submission.domain.SubmissionState.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionTagRepository submissionTagRepository;
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final EvaluationRepository evaluationRepository;

    public SubmissionResponse getSubmissionById(PrincipalDetails auth, Long id) {

        Submission submission = submissionRepository.findByIdJoinFetchMember(id).orElseThrow(() -> new BadRequestException(NOT_FOUND_SUBMISSION));

        if (submission.getVisibility() == SubmissionVisibility.PRIVATE) {
            validatePrivateAccessible(auth, submission);
        }

        return SubmissionResponse.from(submission);
    }

    private void validatePrivateAccessible(PrincipalDetails auth, Submission submission) {

        if (auth == null) {
            throw new UnauthorizedException(PRIVATE_POST);
        }

        Study study = submission.getStudy();
        validateMemberInStudy(study, auth.getId());
    }

    public SubmissionPageResponse getSubmissions(SubmissionCondition condition, Pageable pageable) {

        Page<Submission> page = submissionRepository.findPageByCondition(condition, pageable);
        return SubmissionPageResponse.of(page.getTotalElements(), page.getContent());
    }

    public SubmissionSliceResponse getSubmissionsSlice(SubmissionCondition condition, Pageable pageable) {
        Slice<Submission> slice = submissionRepository.findSliceByCondition(condition, pageable);
        return SubmissionSliceResponse.of(slice.hasNext(), slice.getContent());
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

        Integer weekNumber = getCurrentWeek(study);
        validateMaxLimit(study, member, weekNumber);

        Submission submission = submissionRepository.save(Submission.builder()
                .study(study)
                .member(member)
                .content(request.getContent())
                .subjectNumber(request.getSubjectNumber())
                .visibility(SubmissionVisibility.valueOf(request.getVisibility()))
                .weekNumber(weekNumber)
                .build());

        List<SubmissionTag> submissionTags = SubmissionTag.toListFromTags(submission, tags);
        submissionTagRepository.saveAll(submissionTags);

        //TODO: oneToMany를 배치인서트할 지 고민이 된다...
        submission.addSubmissionTags(submissionTags);

        return submission.getId();
    }

    @Transactional
    public Long evaluate(Long submissionId, Long evaluatorId, CreateEvaluationRequest request) {

        Submission submission = submissionRepository.findByIdJoinFetchStudy(submissionId).orElseThrow(() -> new BadRequestException(NOT_FOUND_SUBMISSION));
        Member evaluator = memberRepository.findById(evaluatorId).orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));
        Study study = submission.getStudy();

        if (submission.getMember().getId().equals(evaluator.getId())) {
            throw new BadRequestException(SELF_EVALUATE);
        }

        validateMemberInStudy(study, evaluator.getId());

        validateDuplicateEvaluation(submission, evaluator);

        Evaluation saved = evaluationRepository.save(Evaluation.builder()
                .submission(submission)
                .member(evaluator)
                .content(request.getContent())
                .passFail(PassFail.valueOf(request.getPassFail()))
                .build());

        updateStateOrNot(submission);

        return saved.getId();
    }

    @Transactional
    public void updateEvaluation(Long submissionId, Long authId, @Valid CreateEvaluationRequest request) {
        Submission submission = submissionRepository.findByIdJoinFetchStudy(submissionId).orElseThrow(() -> new BadRequestException(NOT_FOUND_SUBMISSION));
        Evaluation evaluation = evaluationRepository.findBySubmissionIdAndMemberId(submission.getId(), authId).orElseThrow(() -> new BadRequestException(NOT_FOUND_EVALUATION));

        evaluation.changeEvaluation(request.getContent(), request.getPassFail());

        updateStateOrNot(submission);
    }

    private void validateMaxLimit(Study study, Member member, Integer weekNumber) {

        long count = submissionRepository.countByWeek(study.getId(), member.getId(), weekNumber);

        if (count >= study.getStudyRule().getSubmitPerWeek()) {
            throw new BadRequestException(SUBMISSION_LIMIT_EXCEEDED);
        }
    }

    private void updateStateOrNot(Submission submission) {

        List<Evaluation> evaluations = evaluationRepository.findBySubmission(submission);

        if (evaluations.size() < submission.getStudy().getNumberOfMembers() - 1) {
            return;
        }

        for (Evaluation evaluation : evaluations) {
            if (evaluation.getPassFail() == PassFail.FAIL) {
                submission.changeState(FAILED);
                return;
            }
        }

        submission.changeState(PASSED);
    }

    private void validateDuplicateEvaluation(Submission submission, Member evaluator) {
        if (evaluationRepository.existsBySubmissionAndMember(submission, evaluator)) {
            throw new BadRequestException(DUPLICATE_EVALUATION);
        }
    }

    private void validateStudyInProgress(Study study) {
        if (study.getState() != IN_PROGRESS) {
            throw new BadRequestException(STUDY_CLOSED);
        }
    }

    private void validateMemberInStudy(Study study, Long memberId) {

        Optional<StudyMember> result = study.getStudyMembers().stream()
                .filter(studyMember -> studyMember.getMember().getId().equals(memberId)).findFirst();

        if (result.isEmpty() || result.get().getState() == BANNED) {
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

    public EvaluationsResponse getEvaluations(Long submissionId, Long authId) {

        Submission submission = submissionRepository.findByIdJoinFetchStudy(submissionId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_SUBMISSION));
        Study study = submission.getStudy();

        validateMemberInStudy(study, authId);

        List<Evaluation> evaluations = evaluationRepository.findBySubmission(submission);

        return EvaluationsResponse.from(evaluations);
    }
}
