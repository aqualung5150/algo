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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.seungjoon.algo.exception.ExceptionCode.*;
import static com.seungjoon.algo.study.domain.StudyMemberRole.*;
import static com.seungjoon.algo.study.domain.StudyMemberState.*;
import static com.seungjoon.algo.study.domain.StudyState.FAILED;
import static com.seungjoon.algo.study.domain.StudyState.IN_PROGRESS;

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

    @Transactional
    public Long createStudy(Long authId, CreateStudyRequest request) {

        RecruitPost post = recruitPostRepository.findByIdJoinFetch(request.getRecruitPostId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_POST));
        Member author = post.getMember();
        StudyRule studyRule = post.getStudyRule();
        
        if (post.getState() == RecruitPostState.COMPLETED) {
            throw new BadRequestException(RECRUITMENT_FINISHED);
        }
        
        if (!author.getId().equals(authId)) {
            throw new UnauthorizedException(NOT_OWN_RESOURCE);
        }

        if (studyRule.getNumberOfMembers() < request.getMemberIds().size()) {
            throw new BadRequestException(INVALID_NUMBER_OF_MEMBERS);
        }
        
        List<Applicant> applicants = applicantRepository.findAllByPostIdJoinFetchMember(post.getId());
        validateApplicants(request.getMemberIds(), applicants, author);

        Study study = studyRepository.save(Study.builder()
                .name(request.getName())
                .numberOfMembers(request.getMemberIds().size())
                .studyRule(post.getStudyRule())
                .firstSubmitDate(getFirstSubmitDate(studyRule))
                .lastSubmitDate(getLastSubmitDate(studyRule))
                .state(IN_PROGRESS)
                .build());

        List<StudyMember> studyMembers = createStudyMembers(study, author, applicants);

        study.addStudyMembers(studyMembers);

        post.changeRecruitPostState(RecruitPostState.COMPLETED);
        //TODO: Applicant 삭제?

        return study.getId();
    }

    private void validateApplicants(List<Long> claimedMembers, List<Applicant> applicants, Member author) {

        List<Long> applicantIds = applicants.stream()
                .mapToLong(applicant -> applicant.getMember().getId())
                .boxed()
                .toList();

        claimedMembers.forEach(memberId -> {
            if (!memberId.equals(author.getId()) && !applicantIds.contains(memberId)) {
                throw new BadRequestException(INVALID_APPLICANTS_SELECTION);
            }
        });
    }

    private List<StudyMember> createStudyMembers(Study study, Member author, List<Applicant> applicants) {
        
        List<StudyMember> studyMembers = new ArrayList<>();

        studyMembers.add(studyMemberRepository.save(StudyMember.builder()
                .member(author)
                .study(study)
                .role(LEADER)
                .build()));

        applicants.forEach(applicant -> {
                    studyMembers.add(studyMemberRepository.save(StudyMember.builder()
                            .member(applicant.getMember())
                            .study(study)
                            .role(MEMBER)
                            .build()));
                }
        );

        return studyMembers;
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
    public void closingVote(Long studyId, Long memberId) {

        Study study = studyRepository.findByIdJoinFetch(studyId).orElseThrow(() -> new BadRequestException(NOT_FOUND_STUDY));

        validateStudyInProgress(study);
        validateMemberInStudy(study, memberId);

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));

        List<ClosingVote> votes = closingVoteRepository.findByStudyIdJoinFetch(studyId);

        votes.forEach(closingVote -> {
            if (closingVote.getMember().getId().equals(memberId)) {
                throw new BadRequestException(DUPLICATE_CLOSING_VOTE);
            }
        });

        if (votes.size() + 1L >= study.getNumberOfMembers()) {
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

    @Transactional
    public void banVote(Long studyId, Long voterId, Long targetId) {

        if (voterId.equals(targetId)) {
            throw new BadRequestException(SAME_VOTER_TARGET);
        }

        Study study = studyRepository.findByIdJoinFetch(studyId).orElseThrow(() -> new BadRequestException(NOT_FOUND_STUDY));

        validateStudyInProgress(study);
        validateMemberInStudy(study, voterId);
        validateMemberInStudy(study, targetId);

        Member voter = memberRepository.findById(voterId).orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));
        Member target = memberRepository.findById(targetId).orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));

        if (banVoteRepository.existsByStudyIdAndVoterIdAndTargetId(studyId, voterId, targetId)) {
            throw new BadRequestException(DUPLICATE_BAN_VOTE);
        }

        List<BanVote> targetReceived = banVoteRepository.findByTarget(target);
        if (study.getNumberOfMembers() > 2 && targetReceived.size() >= study.getNumberOfMembers() - 2) {

            updateTargetBanned(study, target.getId());

            study.changeNumberOfMembers(study.getNumberOfMembers() - 1);

            /* 스터디의 모든 투표 초기화 */
            closingVoteRepository.deleteByStudyId(study.getId());
            banVoteRepository.deleteByStudyId(study.getId());

        } else {

            banVoteRepository.save(
                    BanVote.builder()
                            .study(study)
                            .voter(voter)
                            .target(target)
                            .build()
            );
        }
    }

    private void updateTargetBanned(Study study, Long targetId) {
        StudyMember studyTarget = study.getStudyMembers().stream()
                .filter(studyMember -> studyMember.getMember().getId().equals(targetId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));
        studyTarget.changeState(BANNED);
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

//        List<Long> memberIds = studyMembers.stream()
//                .mapToLong(studyMember -> studyMember.getMember().getId())
//                .boxed().toList();
//
//        if (!memberIds.contains(memberId)) {
//            throw new BadRequestException(MEMBER_NOT_IN_STUDY);
//        }
    }
}
