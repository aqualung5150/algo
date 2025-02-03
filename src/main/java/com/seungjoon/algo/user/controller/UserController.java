package com.seungjoon.algo.user.controller;

import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.exception.ExceptionCode;
import com.seungjoon.algo.user.domain.User;
import com.seungjoon.algo.user.dto.CreateUserRequest;
import com.seungjoon.algo.user.dto.UserResponse;
import com.seungjoon.algo.user.repository.UserRepository;
import com.seungjoon.algo.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("{id}")
    public User getUser(@PathVariable long id) {
        return userRepository.findById(id).orElseThrow(() -> new BadRequestException(ExceptionCode.NOT_FOUND_USER));
    }
}
