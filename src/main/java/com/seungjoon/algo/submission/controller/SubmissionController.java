package com.seungjoon.algo.submission.controller;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.submission.dto.SubmissionResponse;
import com.seungjoon.algo.submission.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @GetMapping("{id}")
    public ResponseEntity<SubmissionResponse> getSubmission(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(submissionService.getSubmissionById(principalDetails, id));
    }
}
