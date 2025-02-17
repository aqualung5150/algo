package com.seungjoon.algo.study.service;

import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.exception.UnauthorizedException;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.repository.MemberRepository;
import com.seungjoon.algo.recruit.domain.Applicant;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.recruit.repository.ApplicantRepository;
import com.seungjoon.algo.recruit.repository.RecruitPostRepository;
import com.seungjoon.algo.study.domain.*;
import com.seungjoon.algo.study.dto.CreateStudyRequest;
import com.seungjoon.algo.study.repository.StudyMemberRepository;
import com.seungjoon.algo.study.repository.StudyRepository;
import com.seungjoon.algo.study.repository.StudyRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static com.seungjoon.algo.exception.ExceptionCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyService {

    private final RecruitPostRepository recruitPostRepository;
    private final ApplicantRepository applicantRepository;
    private final StudyRepository studyRepository;
    private final StudyRuleRepository studyRuleRepository;
    private final MemberRepository memberRepository;
    private final StudyMemberRepository studyMemberRepository;

    @Transactional
    public Long createStudy(Long authId, CreateStudyRequest request) {

        RecruitPost post = recruitPostRepository.findByIdJoinFetch(request.getRecruitPostId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_POST));

        StudyRule studyRule = post.getStudyRule();

        validateAuthorization(authId, post);

        validateApplicants(request, post);

        Study study = studyRepository.save(Study.builder()
                .studyRule(post.getStudyRule())
                .firstSubmitDate(getFirstSubmitDate(studyRule))
                .lastSubmitDate(getLastSubmitDate(studyRule))
                .state(StudyState.IN_PROGRESS)
                .build());

        List<StudyMember> studyMembers = getStudyMembers(study, request.getMemberIds(), authId);

        study.addStudyMembers(studyMembers);

        return study.getId();
    }

    private void validateAuthorization(Long authId, RecruitPost post) {
        if (!post.getMember().getId().equals(authId)) {
            throw new UnauthorizedException(NOT_OWN_RESOURCE);
        }
    }

    private void validateApplicants(CreateStudyRequest request, RecruitPost post) {

        if (post.getStudyRule().getNumberOfMembers() <= request.getMemberIds().size()) {
            throw new BadRequestException(INVALID_NUMBER_OF_MEMBERS);
        }

        List<Applicant> applicants = applicantRepository.findAllByPostIdJoinFetchMember(post.getId());

        applicants.forEach(applicant -> {
            if (!request.getMemberIds().contains(applicant.getMember().getId())) {
                throw new BadRequestException(INVALID_APPLICANTS_SELECTION);
            }
        });
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

    private List<StudyMember> getStudyMembers(Study study, List<Long> memberIds, Long authId) {
        return memberIds.stream().map(id -> {

            Member member = memberRepository.findById(id).orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));

            return studyMemberRepository.save(StudyMember.builder()
                    .member(member)
                    .study(study)
                    .role(authId.equals(member.getId()) ? StudyMemberRole.LEADER : StudyMemberRole.MEMBER)
                    .build());
        }).toList();
    }
}
