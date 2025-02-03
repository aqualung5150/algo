package com.seungjoon.algo.member.controller;

import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.exception.ExceptionCode;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.dto.MemberResponse;
import com.seungjoon.algo.member.repository.MemberRepository;
import com.seungjoon.algo.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @GetMapping("{id}")
    public MemberResponse getUser(@PathVariable long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new BadRequestException(ExceptionCode.NOT_FOUND_MEMBER));
        return new MemberResponse(member);
    }
}
