package com.seungjoon.algo.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seungjoon.algo.CustomMockUserSecurityContextFactory;
import com.seungjoon.algo.WithMockMember;
import com.seungjoon.algo.config.SecurityConfig;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.domain.MemberState;
import com.seungjoon.algo.member.domain.Role;
import com.seungjoon.algo.member.repository.MemberRepository;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.recruit.domain.RecruitPostState;
import com.seungjoon.algo.recruit.dto.RecruitPostSliceResponse;
import com.seungjoon.algo.recruit.repository.RecruitPostRepository;
import com.seungjoon.algo.study.domain.StudyRule;
import com.seungjoon.algo.study.repository.StudyRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.DayOfWeek;

import static com.seungjoon.algo.recruit.domain.RecruitPostState.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@ImportAutoConfiguration(exclude = SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
//@EnableAutoConfiguration(exclude = {SecurityConfig.class})
//    @AutoConfiguration()
class MemberControllerTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RecruitPostRepository recruitPostRepository;
    @Autowired
    StudyRuleRepository studyRuleRepository;

//    @MockitoBean /* 기존 시큐리티 비활성화 */
//    SecurityFilterChain securityFilterChain;

//    @TestConfiguration
//    /* 시큐리티 기본 세팅 */
//    static class SecurityTestConfig {
//        @Bean
//        public SecurityFilterChain securityTestFilterChain(HttpSecurity http) throws Exception {
//            return http.build();
//        }
//    }

//    @BeforeEach
//    void init() {
//        Member.builder()
//                .username("member1")
//                .email("member1@gmail.com")
//                .password("password")
//                .role(Role.MEMBER)
//                .state(MemberState.ACTIVE)
//                .authType("normal")
//                .build();
//    }

    @Test
    void getUser() {
    }

    @Test
//    @WithMockMember
    void getRecruitPosts() throws Exception {

        //given
        Member member = memberRepository.save(Member.builder()
                .username("member1")
                .email("member1@gmail.com")
                .password("password")
                .role(Role.MEMBER)
                .state(MemberState.ACTIVE)
                .authType("normal")
                .build());

        for (int i = 0; i < 10; ++i) {
            StudyRule studyRule = StudyRule.builder().numberOfMembers(3).submitDayOfWeek(DayOfWeek.FRIDAY).submitPerWeek(3).totalWeek(3).build();
            studyRuleRepository.save(studyRule);
            RecruitPost recruitPost = RecruitPost.builder().title("post" + i).member(member).studyRule(studyRule).build();
            recruitPostRepository.save(recruitPost);
        }

        //when
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get("/members/" + member.getId() + "/recruit-posts"));

        //then
        MvcResult mvcResult = actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
//
//        RecruitPostSliceResponse recruitPostSliceResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), RecruitPostSliceResponse.class);
//        System.out.println(recruitPostSliceResponse);
    }

    @Test
    @WithMockMember
    void getApplications() {
    }

    @Test
    @WithMockMember
    void getStudies() {
    }
}