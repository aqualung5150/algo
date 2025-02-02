package com.seungjoon.algo.auth.service;

import com.seungjoon.algo.auth.dto.SignUpRequest;
import com.seungjoon.algo.auth.oauth.dto.SetUsernameRequest;
import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.exception.ExceptionCode;
import com.seungjoon.algo.exception.ExistingAuthTypeException;
import com.seungjoon.algo.user.domain.Role;
import com.seungjoon.algo.user.domain.User;
import com.seungjoon.algo.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.seungjoon.algo.exception.ExceptionCode.*;
import static com.seungjoon.algo.user.domain.Role.MEMBER;
import static com.seungjoon.algo.user.domain.UserState.ACTIVE;

@Slf4j
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public User signUp(SignUpRequest signUpRequest) {

        userRepository.findByEmail(signUpRequest.getEmail()).ifPresent(user -> {
            ExceptionCode exceptionCode = getExistingAuthTypeExceptionCode(user.getAuthType());
            throw new ExistingAuthTypeException(exceptionCode);
        });

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new BadRequestException(USERNAME_ALREADY_EXIST);
        }

        return userRepository.save(User.builder()
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
            return EXISTING_GOOGLE_USER;
        }

        if (authType.equals("naver")) {
            return EXISTING_NAVER_USER;
        }

        return EXISTING_NORMAL_USER;
    }

    public User setUsername(Long userId, SetUsernameRequest request) {

        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException(NOT_FOUND_USER));

        user.changeUsername(request.getUsername());
        user.changeRole(Role.MEMBER);

        return user;
    }
}
