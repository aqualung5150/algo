package com.seungjoon.algo.member.service;

import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.exception.ExceptionCode;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getById(Long id) {

        return memberRepository
                .findById(id)
                .orElseThrow(() -> new BadRequestException(ExceptionCode.NOT_FOUND_MEMBER));
    }
}
