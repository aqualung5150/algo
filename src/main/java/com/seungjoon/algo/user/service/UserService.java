package com.seungjoon.algo.user.service;

import com.seungjoon.algo.user.domain.Role;
import com.seungjoon.algo.user.domain.User;
import com.seungjoon.algo.user.domain.UserState;
import com.seungjoon.algo.user.dto.CreateUserRequest;
import com.seungjoon.algo.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(@Valid CreateUserRequest request) {

        return userRepository.save(
                User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .role(Role.MEMBER)
                .state(UserState.ACTIVE)
                .build()
        );
    }
}
