package com.seungjoon.algo.study.controller;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.study.dto.*;
import com.seungjoon.algo.study.service.StudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/study")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @PostMapping
    public ResponseEntity<Void> create(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody CreateStudyRequest request
    ) {
        Long id = studyService.createStudy(principalDetails.getId(), request);

        return ResponseEntity.created(URI.create("/study/" + id)).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<StudyResponse> getStudy(@PathVariable Long id) {
        StudyResponse studyResponse = studyService.getStudyById(id);

        return ResponseEntity.ok(studyResponse);
    }

    @GetMapping("{id}/closing-vote")
    public ResponseEntity<ClosingVoteResponse> getCloseVoteCount(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ClosingVoteResponse(studyService.countClosingVote(id))
        );
    }

    @PostMapping("{id}/closing-vote")
    public ResponseEntity<Void> createClosingVote(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long id
    ) {
        studyService.voteClosing(id, principalDetails.getId());

        return ResponseEntity.noContent().build();
    }

    /*TODO: 종료투표 취소 가능하게 해야 할까? */
//    @DeleteMapping("{id}/closing-vote")
//    public ResponseEntity<Void> cancelClosingVote(
//            @AuthenticationPrincipal PrincipalDetails principalDetails,
//            @PathVariable Long id
//    ) {
//        studyService.cancelClosingVote(id, principalDetails.getId());
//
//        return ResponseEntity.noContent().build();
//    }

    @PostMapping("{studyId}/ban-vote/{targetId}")
    public ResponseEntity<Void> createBanVote(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long studyId,
            @PathVariable Long targetId
    ) {
        studyService.banVote(studyId, principalDetails.getId(), targetId);

        return ResponseEntity.noContent().build();
    }

    /* Submission */
    @PostMapping("{id}/submissions")
    public ResponseEntity<Void> createSubmission(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long id,
            @Valid @RequestBody CreateSubmissionRequest request
    ) {

        Long submissionId = studyService.submit(id, principalDetails.getId(), request);

        return ResponseEntity.created(URI.create("/study/" + id + "/submissions/" + submissionId)).build();
    }

//    @GetMapping("{studyId}/submissions")
//    public ResponseEntity<SubmissionResponse> getSubmission(
//            @PathVariable Long studyId,
//    ) {
//
//        return ResponseEntity.ok(studyService.);
//    }
}
