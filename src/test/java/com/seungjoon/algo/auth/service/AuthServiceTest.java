package com.seungjoon.algo.auth.service;

import com.seungjoon.algo.auth.dto.SignUpRequest;
import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.exception.ExistingAuthTypeException;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void signUp() {
        //given
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("spring@gmail.com");
        signUpRequest.setUsername("spring");
        signUpRequest.setPassword("123456");
        //when
        Member savedMember = authService.signUp(signUpRequest);
        Member findMember = memberRepository.findById(savedMember.getId()).orElse(null);
        //then
        assertThat(findMember).isNotNull();
        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getEmail()).isEqualTo(savedMember.getEmail());
        assertThat(findMember.getPassword()).isEqualTo(savedMember.getPassword());
        assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
    }

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
        assertThatThrownBy(() -> authService.signUp(user2))
                .isInstanceOf(BadRequestException.class)
                .extracting("code")
                .isEqualTo(1006);
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
        assertThatThrownBy(() -> authService.signUp(user2))
                .isInstanceOf(ExistingAuthTypeException.class)
                .extracting("code")
                .isEqualTo(1008);
    }
}