package com.seungjoon.algo.auth.service;

import com.seungjoon.algo.auth.dto.SignUpRequest;
import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.exception.ExistingAuthTypeException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void signUpExistUsername() {
        //given
        SignUpRequest user1 = new SignUpRequest();
        user1.setEmail("spring@gmail.com");
        user1.setUsername("spring");
        user1.setPassword("123456");

        authService.signUp(user1);

        //when
        SignUpRequest user2 = new SignUpRequest();
        user2.setEmail("java@gmail.com");
        user2.setUsername("spring");
        user2.setPassword("123456");


        //then
        BadRequestException ex = catchThrowableOfType(
                BadRequestException.class,
                () -> authService.signUp(user2)
        );
        assertThat(ex.getCode()).isEqualTo(1006);
    }

    @Test
    void signUpExistEmail() {
        //given
        SignUpRequest user1 = new SignUpRequest();
        user1.setEmail("spring@gmail.com");
        user1.setUsername("spring");
        user1.setPassword("123456");

        authService.signUp(user1);

        //when
        SignUpRequest user2 = new SignUpRequest();
        user2.setEmail("spring@gmail.com");
        user2.setUsername("java");
        user2.setPassword("123456");


        //then
        ExistingAuthTypeException ex = catchThrowableOfType(
                ExistingAuthTypeException.class,
                () -> authService.signUp(user2)
        );
        assertThat(ex.getCode()).isEqualTo(1008);
    }
}