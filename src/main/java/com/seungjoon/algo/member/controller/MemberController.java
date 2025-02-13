package com.seungjoon.algo.member.controller;

import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.dto.ProfileResponse;
import com.seungjoon.algo.member.service.MemberService;
import com.seungjoon.algo.recruit.dto.RecruitPostPageResponse;
import com.seungjoon.algo.recruit.dto.RecruitPostSliceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("{id}")
    public ResponseEntity<ProfileResponse> getUser(@PathVariable Long id) {
        Member member = memberService.getById(id);
        return ResponseEntity.ok(ProfileResponse.from(member));
    }

    //TODO - "{id}/recruit-posts"

    @GetMapping("{id}/applications")
    public ResponseEntity<RecruitPostSliceResponse> getApplications(
            @PathVariable Long id,
            @PageableDefault(size = 20, sort = "createdDate", direction = DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(memberService.getApplicatedPostList(id, pageable));
    }
}
