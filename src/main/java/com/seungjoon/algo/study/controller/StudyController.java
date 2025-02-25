package com.seungjoon.algo.study.controller;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.study.dto.CreateStudyRequest;
import com.seungjoon.algo.study.dto.StudyResponse;
import com.seungjoon.algo.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @RequestBody CreateStudyRequest request
    ) {
        Long id = studyService.createStudy(principalDetails.getId(), request);

        return ResponseEntity.created(URI.create("/study/" + id)).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<StudyResponse> getStudy(@PathVariable Long id) {
        StudyResponse studyResponse = studyService.getStudyById(id);

        return ResponseEntity.ok(studyResponse);
    }
}
