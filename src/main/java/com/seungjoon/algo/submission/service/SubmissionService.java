package com.seungjoon.algo.submission.service;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.exception.UnauthorizedException;
import com.seungjoon.algo.study.domain.StudyMember;
import com.seungjoon.algo.submission.domain.Submission;
import com.seungjoon.algo.submission.domain.SubmissionVisibility;
import com.seungjoon.algo.submission.dto.SubmissionCondition;
import com.seungjoon.algo.submission.dto.SubmissionPageResponse;
import com.seungjoon.algo.submission.dto.SubmissionResponse;
import com.seungjoon.algo.submission.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.seungjoon.algo.exception.ExceptionCode.NOT_FOUND_POST;
import static com.seungjoon.algo.exception.ExceptionCode.PRIVATE_POST;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;

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
}
