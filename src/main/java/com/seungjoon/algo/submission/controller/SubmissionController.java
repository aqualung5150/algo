package com.seungjoon.algo.submission.controller;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.submission.dto.*;
import com.seungjoon.algo.submission.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<Map<String, Long>> submit(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody CreateSubmissionRequest request
    ) {
        Long id = submissionService.submit(principalDetails.getId(), request);
        return ResponseEntity.ok(Map.of("id", id));
    }

    @GetMapping
    public ResponseEntity<SubmissionPageResponse> getSubmissions(
            @ModelAttribute SubmissionCondition condition,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(submissionService.getSubmissions(condition, pageable));
    }

    @GetMapping("slice")
    public ResponseEntity<SubmissionSliceResponse> getSubmissionsSlice(
            @ModelAttribute SubmissionCondition condition,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(submissionService.getSubmissionsSlice(condition, pageable));
    }

    @GetMapping("{id}")
    public ResponseEntity<SubmissionResponse> getSubmission(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(submissionService.getSubmissionById(principalDetails, id));
    }

    @GetMapping("{id}/evaluations")
    public ResponseEntity<EvaluationsResponse> getEvaluations(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(submissionService.getEvaluations(id, principalDetails.getId()));
    }

    @PostMapping("{id}/evaluations")
    public ResponseEntity<Void> evaluate(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long id,
            @Valid @RequestBody CreateEvaluationRequest request
    ) {

        Long evaluationId = submissionService.evaluate(id, principalDetails.getId(), request);
        return ResponseEntity.created(URI.create("/submissions/" + id + "/evaluations/" + evaluationId)).build();
    }

    @PutMapping("{id}/evaluations")
    public ResponseEntity<Void> updateEvaluation(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long id,
            @Valid @RequestBody CreateEvaluationRequest request
    ) {
        submissionService.updateEvaluation(id, principalDetails.getId(), request);
        return ResponseEntity.ok().build();
    }
}
