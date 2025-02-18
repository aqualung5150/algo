package com.seungjoon.algo.recruit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seungjoon.algo.WithMockMember;
import com.seungjoon.algo.recruit.dto.CreateRecruitPostRequest;
import com.seungjoon.algo.recruit.repository.RecruitPostRepository;
import com.seungjoon.algo.recruit.service.RecruitPostService;
import com.seungjoon.algo.study.domain.Study;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

/* SecurityFilter의 기본값이 세팅됨 */
//@Import(SecurityConfig.class) //사용되는 관련 빈을 모두 등록해줘야함 -> 시큐리티필터 테스트는 @SpringBootTest를 통해 하자
@WebMvcTest(RecruitPostController.class)
class RecruitPostControllerTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecruitPostService recruitPostService;

    @Test
    @WithMockMember
    void createRecruitPost() throws Exception {
        //given
        given(recruitPostService.createRecruitPost(anyLong(), any(CreateRecruitPostRequest.class)))
                .willReturn(1L);
        //when
        CreateRecruitPostRequest createRecruitPostRequest = new CreateRecruitPostRequest(1L,
                "anyTitle",
                "anyContent",
                1,
                10,
                15,
                2,
                "FRIDAY",
                2,
                List.of(1L, 2L)
        );
        ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders.post("/recruit-posts")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRecruitPostRequest))
        );

        //then
        actions
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.LOCATION, "/recruit-posts/1"));
    }

    @Test
    void createApplicant() {
        //given
//        given(recruitPostService.createApplicant(anyLong(), anyLong()))
//                .willReturn()
        //when

        //then
    }
}