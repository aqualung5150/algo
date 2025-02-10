package com.seungjoon.algo.recruit.controller;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.recruit.dto.ApplicantProfileSliceResponse;
import com.seungjoon.algo.recruit.dto.CreateRecruitPostRequest;
import com.seungjoon.algo.recruit.dto.RecruitPostPageResponse;
import com.seungjoon.algo.recruit.dto.RecruitPostResponse;
import com.seungjoon.algo.recruit.service.RecruitPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/recruit-posts")
@RequiredArgsConstructor
public class RecruitPostController {

    private final RecruitPostService recruitPostService;

    //TODO - 필터 컨디션: By 내 지원서 등등
    @GetMapping
    public RecruitPostPageResponse getRecruitPosts(
            @PageableDefault(size = 20, sort = "id", direction = DESC) Pageable pageable
    ) {

        return recruitPostService.getRecruitPostList(pageable);
    }

    @GetMapping("{id}")
    public ResponseEntity<RecruitPostResponse> getRecruitPost(@PathVariable Long id) {

        RecruitPost post = recruitPostService.getRecruitPostById(id);
        return ResponseEntity.ok().body(RecruitPostResponse.from(post));
    }

    @PostMapping
    public ResponseEntity<Void> createRecruitPost(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody CreateRecruitPostRequest request
    ) {

        Long id = recruitPostService.createRecruitPost(principalDetails.getId(), request);
        return ResponseEntity.created(URI.create("/recruit-posts/" + id)).build();
    }

    @RequestMapping(value = "{postId}/applicants/{memberId}", method = HEAD)
    public ResponseEntity<Void> existsApplicant(
            @PathVariable Long postId,
            @PathVariable Long memberId
    ) {
        recruitPostService.existsByRecruitPostIdAndMemberId(postId, memberId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{postId}/applicants")
    public ResponseEntity<ApplicantProfileSliceResponse> getApplicantList(
            @PathVariable Long postId,
            @PageableDefault(size = 20, sort = "createdDate", direction = ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(recruitPostService.getApplicantProfileListByPostId(postId, pageable));
    }

    @PostMapping("{postId}/applicants")
    public ResponseEntity<Void> createApplicant(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long postId
    ) {
        recruitPostService.createApplicant(postId, principalDetails.getId());

        return ResponseEntity.created(URI.create("/recruit-posts/" + postId)).build();
    }
}
