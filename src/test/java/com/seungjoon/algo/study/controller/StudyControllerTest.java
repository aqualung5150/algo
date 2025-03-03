package com.seungjoon.algo.study.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seungjoon.algo.WithMockMember;
import com.seungjoon.algo.auth.dto.SignUpRequest;
import com.seungjoon.algo.auth.jwt.JwtFilter;
import com.seungjoon.algo.auth.service.AuthService;
import com.seungjoon.algo.recruit.dto.CreateRecruitPostRequest;
import com.seungjoon.algo.recruit.service.RecruitPostService;
import com.seungjoon.algo.study.domain.Study;
import com.seungjoon.algo.study.dto.CreateStudyRequest;
import com.seungjoon.algo.study.repository.StudyRepository;
import com.seungjoon.algo.submission.domain.Tag;
import com.seungjoon.algo.submission.repository.TagRepository;
import jakarta.servlet.FilterChain;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.willAnswer;

@SpringBootTest
@AutoConfigureMockMvc
class StudyControllerTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    JwtFilter jwtFilter;

    @Autowired
    StudyRepository studyRepository;
    @Autowired
    RecruitPostService recruitPostService;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    AuthService authService;

    @BeforeEach
    void init() throws Exception {
        willAnswer(invocation -> {
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).given(jwtFilter).doFilter(any(), any(), any());
    }

    @Test
    @WithMockMember(id = 1)
    void createStudy() throws Exception {
        //given
        tagRepository.save(new Tag(1L, "dp"));
        tagRepository.save(new Tag(2L, "dfs"));
        tagRepository.save(new Tag(3L, "bfs"));

        for (int i = 1; i <= 3; ++i) {
            SignUpRequest signUpRequest = new SignUpRequest();
            signUpRequest.setEmail("test@naver.com" + i);
            signUpRequest.setUsername("test" + i);
            signUpRequest.setPassword("1234Q1234q!");
            authService.signUp(signUpRequest);
        }

        Long postId = recruitPostService.createRecruitPost(1L, new CreateRecruitPostRequest(
                "post1",
                "content1",
                3,
                10,
                15,
                4,
                "FRIDAY",
                4,
                List.of(1L, 2L)
        ));

        recruitPostService.createApplicant(postId, 2L);
        recruitPostService.createApplicant(postId, 3L);

        //when
        CreateStudyRequest createStudyRequest = new CreateStudyRequest(
                1L,
                "study1",
                List.of(1L, 2L, 3L)
        );

        ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders.post("/study")
//                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createStudyRequest))
        );

        //then
        String location = actions
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getHeader(HttpHeaders.LOCATION);

        String[] split = location.split("/");
        Long studyId = Long.valueOf(split[split.length - 1]);
        Study study = studyRepository.findById(studyId).orElse(null);
        Assertions.assertThat(study).isNotNull();
    }
}