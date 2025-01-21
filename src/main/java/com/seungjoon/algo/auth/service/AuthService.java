package com.seungjoon.algo.auth.service;

import com.seungjoon.algo.auth.dto.LoginRequest;
import com.seungjoon.algo.auth.dto.SignUpRequest;
import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.exception.UnauthorizedException;
import com.seungjoon.algo.user.domain.User;
import com.seungjoon.algo.user.domain.UserState;
import com.seungjoon.algo.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.seungjoon.algo.exception.ExceptionCode.*;
import static com.seungjoon.algo.user.domain.Role.MEMBER;

@Slf4j
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User signUp(SignUpRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(EMAIL_ALREADY_EXIST);
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException(USERNAME_ALREADY_EXIST);
        }

        return userRepository.save(User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(MEMBER)
                .username(request.getUsername())
                .state(UserState.ACTIVE)
                .build());
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new BadRequestException(NOT_FOUND_USER));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException(INVALID_PASSWORD);
        }

//        log.info("로그인 성공");
        return user;
    }
}
