package com.seungjoon.algo.auth.service;

import com.seungjoon.algo.auth.dto.SignUpRequest;
import com.seungjoon.algo.auth.oauth.dto.SetUsernameRequest;
import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.exception.ExceptionCode;
import com.seungjoon.algo.exception.ExistingAuthTypeException;
import com.seungjoon.algo.member.domain.Role;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.seungjoon.algo.exception.ExceptionCode.*;
import static com.seungjoon.algo.member.domain.Role.MEMBER;
import static com.seungjoon.algo.member.domain.MemberState.ACTIVE;

@Slf4j
@Service
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public Member signUp(SignUpRequest signUpRequest) {

        memberRepository.findByEmail(signUpRequest.getEmail()).ifPresent(user -> {
            ExceptionCode exceptionCode = getExistingAuthTypeExceptionCode(user.getAuthType());
            throw new ExistingAuthTypeException(exceptionCode);
        });

        if (memberRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new BadRequestException(USERNAME_ALREADY_EXIST);
        }

        return memberRepository.save(Member.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .authType("normal")
                .role(MEMBER)
                .username(signUpRequest.getUsername())
                .state(ACTIVE)
                .build());
    }

    private ExceptionCode getExistingAuthTypeExceptionCode(String authType) {
        if (authType.equals("google")) {
            return EXISTING_GOOGLE_MEMBER;
        }

        if (authType.equals("naver")) {
            return EXISTING_NAVER_MEMBER;
        }

        return EXISTING_NORMAL_MEMBER;
    }

    public Member setUsername(Long memberId, SetUsernameRequest request) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER));

        member.changeUsername(request.getUsername());
        member.changeRole(Role.MEMBER);

        return member;
    }
}
