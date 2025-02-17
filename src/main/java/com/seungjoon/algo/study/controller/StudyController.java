package com.seungjoon.algo.study.controller;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.study.domain.StudyRule;
import com.seungjoon.algo.study.dto.CreateStudyRequest;
import com.seungjoon.algo.study.service.StudyService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
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

    //TODO: test
    @PostMapping
    public ResponseEntity<Void> create(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody CreateStudyRequest request
    ) {
        Long id = studyService.createStudy(principalDetails.getId(), request);

        return ResponseEntity.created(URI.create("/study/" + id)).build();
    }
}
