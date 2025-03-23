package com.seungjoon.algo.study.controller;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.study.dto.ClosingVoteResponse;
import com.seungjoon.algo.study.dto.CreateStudyRequest;
import com.seungjoon.algo.study.dto.StudyResponse;
import com.seungjoon.algo.study.service.StudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("study")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @PostMapping
    public ResponseEntity<Map<String, Long>> create(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody CreateStudyRequest request
    ) {
        Long id = studyService.createStudy(principalDetails.getId(), request);

        return ResponseEntity.ok(Map.of("id", id));
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
        studyService.closingVote(id, principalDetails.getId());

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
}
