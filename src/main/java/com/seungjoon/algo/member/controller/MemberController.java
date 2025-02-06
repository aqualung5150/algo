package com.seungjoon.algo.member.controller;

import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.dto.MemberResponse;
import com.seungjoon.algo.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("{id}")
    public MemberResponse getUser(@PathVariable Long id) {
        Member member = memberService.getById(id);
        return MemberResponse.from(member);
    }
}
