package com.seungjoon.algo.auth.service;

import com.seungjoon.algo.auth.dto.EmailValidationRequest;
import com.seungjoon.algo.auth.dto.SignUpRequest;
import com.seungjoon.algo.auth.jwt.JwtProvider;
import com.seungjoon.algo.auth.oauth.dto.SetUsernameRequest;
import com.seungjoon.algo.exception.*;
import com.seungjoon.algo.member.domain.Role;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.seungjoon.algo.auth.jwt.JwtType.ACCESS;
import static com.seungjoon.algo.auth.jwt.JwtType.REFRESH;
import static com.seungjoon.algo.exception.ExceptionCode.*;
import static com.seungjoon.algo.member.domain.Role.MEMBER;
import static com.seungjoon.algo.member.domain.MemberState.ACTIVE;

@Slf4j
@Service
@Transactional
public class AuthService {

    @Value("${cloudfront.base-url}")
    private String baseUrl;

    @Value("${jwt.access-expire}")
    private Long accessExpire;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(MemberRepository memberRepository, JwtProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        this.jwtProvider = jwtProvider;
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
                .username(signUpRequest.getUsername())
                .role(MEMBER)
                .imageUrl(baseUrl + "images/default.jpg")
                .authType("normal")
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

    public void validateEmail(EmailValidationRequest request) {

        boolean exist = memberRepository.existsByEmail(request.getEmail());
        if (exist) {
            throw new BadRequestException(EMAIL_ALREADY_EXIST);
        }
    }

    public String reissue(String refreshToken) {

        if (refreshToken == null) {
            throw new MissingJwtTokenException(MISSING_JWT_TOKEN);
        }

        try {
            return jwtProvider.generateToken(
                    ACCESS,
                    jwtProvider.getId(REFRESH, refreshToken),
                    jwtProvider.getRole(REFRESH, refreshToken),
                    accessExpire
            );
        } catch (Exception e) {
            throw new UnauthorizedException(INVALID_JWT_TOKEN);
        }
    }
}
