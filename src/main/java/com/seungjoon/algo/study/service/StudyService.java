package com.seungjoon.algo.study.service;

import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.exception.UnauthorizedException;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.repository.MemberRepository;
import com.seungjoon.algo.recruit.domain.Applicant;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.recruit.domain.RecruitPostState;
import com.seungjoon.algo.recruit.repository.ApplicantRepository;
import com.seungjoon.algo.recruit.repository.RecruitPostRepository;
import com.seungjoon.algo.study.domain.*;
import com.seungjoon.algo.study.dto.CreateStudyRequest;
import com.seungjoon.algo.study.dto.StudyPageResponse;
import com.seungjoon.algo.study.dto.StudyResponse;
import com.seungjoon.algo.study.repository.BanVoteRepository;
import com.seungjoon.algo.study.repository.ClosingVoteRepository;
import com.seungjoon.algo.study.repository.StudyMemberRepository;
import com.seungjoon.algo.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static com.seungjoon.algo.exception.ExceptionCode.*;
import static com.seungjoon.algo.study.domain.StudyState.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyService {

    private final RecruitPostRepository recruitPostRepository;
    private final ApplicantRepository applicantRepository;
    private final StudyRepository studyRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final ClosingVoteRepository closingVoteRepository;
    private final MemberRepository memberRepository;
    private final BanVoteRepository banVoteRepository;

    public StudyResponse getStudyById(Long id) {

        return StudyResponse.from(studyRepository.findByIdJoinFetch(id)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_STUDY)));
    }

    public StudyPageResponse getStudiesByMemberId(Long memberId, Long authId, Pageable pageable) {
        if (!memberId.equals(authId)) {
            throw new UnauthorizedException(NOT_OWN_RESOURCE);
        }

        Page<Study> studies = studyRepository.findByMemberId(memberId, pageable);

        return StudyPageResponse.of(studies.getTotalElements(), studies.getContent());
    }

    //TODO: 팀 인원 어떻게 | 멤버id 중복 못 넣게
    @Transactional
    public Long createStudy(Long authId, CreateStudyRequest request) {

        RecruitPost post = recruitPostRepository.findByIdJoinFetch(request.getRecruitPostId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_POST));

        StudyRule studyRule = post.getStudyRule();

        validateAuthorization(authId, post);
        validateNumberOfMembers(request.getMemberIds(), post);
        List<Applicant> applicants = applicantRepository.findAllByPostIdJoinFetchMember(post.getId());
        validateApplicants(request.getMemberIds(), applicants);

        Study study = studyRepository.save(Study.builder()
                .name(request.getName())
                .studyRule(post.getStudyRule())
                .firstSubmitDate(getFirstSubmitDate(studyRule))
                .lastSubmitDate(getLastSubmitDate(studyRule))
                .state(IN_PROGRESS)
                .build());

        List<StudyMember> studyMembers = createStudyMembers(study, applicants, authId);

        study.addStudyMembers(studyMembers);

        post.changeRecruitPostState(RecruitPostState.COMPLETED);

        return study.getId();
    }

    private void validateAuthorization(Long authId, RecruitPost post) {
        if (!post.getMember().getId().equals(authId)) {
            throw new UnauthorizedException(NOT_OWN_RESOURCE);
        }
    }

    private void validateNumberOfMembers(List<Long> memberIds, RecruitPost post) {

        if (post.getStudyRule().getNumberOfMembers() < memberIds.size()) {
            throw new BadRequestException(INVALID_NUMBER_OF_MEMBERS);
        }
    }

    private void validateApplicants(List<Long> memberIds, List<Applicant> applicants) {

        applicants.forEach(applicant -> {
            if (!memberIds.contains(applicant.getMember().getId())) {
                throw new BadRequestException(INVALID_APPLICANTS_SELECTION);
            }
        });
    }

    private List<StudyMember> createStudyMembers(Study study, List<Applicant> applicants, Long authId) {
        return applicants.stream().map(applicant ->
                studyMemberRepository.save(StudyMember.builder()
                        .member(applicant.getMember())
                        .study(study)
                        .role(
                                authId.equals(applicant.getMember().getId()) ?
                                        StudyMemberRole.LEADER :
                                        StudyMemberRole.MEMBER
                        )
                        .build())
        ).toList();
    }

    private LocalDate getFirstSubmitDate(StudyRule studyRule) {
        return LocalDate.now()
                .with(TemporalAdjusters.nextOrSame(
                        studyRule.getSubmitDayOfWeek())
                );
    }

    private LocalDate getLastSubmitDate(StudyRule studyRule) {
        return LocalDate.now()
                .plusWeeks(studyRule.getTotalWeek());
    }

    public Long countClosingVote(Long studyId) {
        return closingVoteRepository.countByStudyId(studyId);
    }

    @Transactional
    public void voteClosing(Long studyId, Long memberId) {

        Study study = studyRepository.findByIdJoinFetch(studyId).orElseThrow(() -> new BadRequestException(NOT_FOUND_STUDY));
        validateStudyInProgress(study);

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));

        List<ClosingVote> votes = closingVoteRepository.findByStudyIdJoinFetch(studyId);

        votes.forEach(closingVote -> {
            if (closingVote.getMember().getId().equals(memberId)) {
                throw new BadRequestException(DUPLICATE_CLOSING_VOTE);
            }
        });

        if (votes.size() + 1L >= study.getStudyMembers().size()) {
            study.changeState(FAILED);
            closingVoteRepository.deleteByStudyId(study.getId());
        } else {
            closingVoteRepository.save(
                    ClosingVote.builder()
                            .study(study)
                            .member(member)
                            .build()
            );
        }
    }

    public void banVote(Long studyId, Long voterId, Long targetId) {

        Study study = studyRepository.findById(studyId).orElseThrow(() -> new BadRequestException(NOT_FOUND_STUDY));
        validateStudyInProgress(study);

        if (banVoteRepository.existsByStudyIdAndVoterIdAndTargetId(studyId, voterId, targetId)) {
            throw new BadRequestException(DUPLICATE_BAN_VOTE);
        }


    }

    private void validateStudyInProgress(Study study) {
        if (study.getState() != IN_PROGRESS) {
            throw new BadRequestException(STUDY_CLOSED);
        }
    }

}
