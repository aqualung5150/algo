package com.seungjoon.algo.recruit.controller;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.recruit.dto.CreateRecruitPostRequest;
import com.seungjoon.algo.recruit.dto.RecruitPostResponse;
import com.seungjoon.algo.recruit.service.RecruitPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/recruit-post")
@RequiredArgsConstructor
public class RecruitPostController {

    private final RecruitPostService recruitPostService;

    //TODO - DTO, 페이징
//    @GetMapping
//    public List<RecruitPost> getRecruitPosts() {
//        return recruitPostService.findAll();
//    }

    @GetMapping("{id}")
    public ResponseEntity<RecruitPostResponse> getRecruitPost(@PathVariable Long id) {
        RecruitPost post = recruitPostService.getById(id);
        return ResponseEntity.ok().body(RecruitPostResponse.from(post));
    }

    @PostMapping
    public ResponseEntity<Void> createRecruitPost(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody CreateRecruitPostRequest request
    ) {
        RecruitPost post = recruitPostService.create(principalDetails.getId(), request);
        return ResponseEntity.created(URI.create("/recruit-post/" + post.getId())).build();
    }
}
