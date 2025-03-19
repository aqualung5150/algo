package com.seungjoon.algo.member.service;

import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.dto.UpdateMemberRequest;
import com.seungjoon.algo.member.repository.MemberRepository;
import com.seungjoon.algo.recruit.repository.ApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.seungjoon.algo.exception.ExceptionCode.NOT_FOUND_MEMBER;
import static com.seungjoon.algo.exception.ExceptionCode.USERNAME_ALREADY_EXIST;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ApplicantRepository applicantRepository;

    public Member getById(Long id) {

        return memberRepository
                .findById(id)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));
    }

    @Transactional
    public Member updateById(Long id, UpdateMemberRequest request) {
        Member member = getById(id);

        duplicateUsername(member.getUsername(), request.getUsername());

        member.updateMember(request.getUsername(), request.getImageUrl());

        return member;
    }

    private void duplicateUsername(String from, String to) {
        if (!from.equals(to) && memberRepository.existsByUsername(to)) {
            throw new BadRequestException(USERNAME_ALREADY_EXIST);
        }
    }
}
