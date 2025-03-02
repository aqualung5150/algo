package com.seungjoon.algo.submission.controller;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.submission.dto.CreateSubmissionRequest;
import com.seungjoon.algo.submission.dto.SubmissionCondition;
import com.seungjoon.algo.submission.dto.SubmissionPageResponse;
import com.seungjoon.algo.submission.dto.SubmissionResponse;
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

@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @GetMapping
    public ResponseEntity<SubmissionPageResponse> getSubmissions(
            @ModelAttribute SubmissionCondition condition,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(submissionService.getSubmissions(condition, pageable));
    }

    @GetMapping("{id}")
    public ResponseEntity<SubmissionResponse> getSubmission(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(submissionService.getSubmissionById(principalDetails, id));
    }

    @PostMapping
    public ResponseEntity<Void> submit(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody CreateSubmissionRequest request
    ) {
        Long submitId = submissionService.submit(principalDetails.getId(), request);
        return ResponseEntity.created(URI.create("/submissions/" + submitId)).build();
    }
}
