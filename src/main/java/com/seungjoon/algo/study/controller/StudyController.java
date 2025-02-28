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

    /*
    TODO: 인증이 필요없는 uri로 설정하면 비공개 게시글일때 어떻게 구분하지???
     */
    @GetMapping("{studyId}/submissions/{submissionId}")
    public ResponseEntity<SubmissionResponse> getStudySubmissions(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long studyId,
            @PathVariable Long submissionId
    ) {

//        boolean authenticated = SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof PrincipalDetails;
////        System.out.println(principal);
////        System.out.println(principal.getClass().getName());
//        if (!authenticated) {
//            System.out.println("Not authenticated");
//        } else {
//            System.out.println("Authenticated");
//        }
        if (principalDetails == null) {
            System.out.println("principal details is null");
        } else {
            System.out.println("principal details is " + principalDetails);
        }

        return null;
    }
}
