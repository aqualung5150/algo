package com.seungjoon.algo.member.controller;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.dto.ProfileResponse;
import com.seungjoon.algo.member.service.MemberService;
import com.seungjoon.algo.recruit.dto.RecruitPostSliceResponse;
import com.seungjoon.algo.recruit.service.RecruitPostService;
import com.seungjoon.algo.study.dto.StudyPageResponse;
import com.seungjoon.algo.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.domain.Sort.Direction.DESC;

/*
TODO
회원탈퇴
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final StudyService studyService;
    private final RecruitPostService recruitPostService;

    @GetMapping("{id}")
    public ResponseEntity<ProfileResponse> getMember(@PathVariable Long id) {
        Member member = memberService.getById(id);
        return ResponseEntity.ok(ProfileResponse.from(member));
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Member member = memberService.getById(principalDetails.getId());
        return ResponseEntity.ok(ProfileResponse.from(member));
    }

    @GetMapping("{id}/recruit-posts")
    public ResponseEntity<RecruitPostSliceResponse> getRecruitPosts(
            @PathVariable Long id,
            @PageableDefault(size = 20, sort = "createdDate", direction = DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(recruitPostService.getByMemberId(id, pageable));
    }

    @GetMapping("{id}/applications")
    public ResponseEntity<RecruitPostSliceResponse> getApplications(
            @PathVariable Long id,
            @PageableDefault(size = 20, sort = "createdDate", direction = DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(recruitPostService.getByApplicantMemberId(id, pageable));
    }

    @GetMapping("{id}/studies")
    public ResponseEntity<StudyPageResponse> getStudies(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long id,
            @PageableDefault(size = 20, sort = "createdDate", direction = DESC) Pageable pageable
    ) {

        return ResponseEntity.ok(studyService.getStudiesByMemberId(id, principalDetails.getId(), pageable));
    }
}
