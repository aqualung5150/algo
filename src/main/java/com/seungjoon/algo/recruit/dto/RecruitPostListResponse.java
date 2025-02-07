package com.seungjoon.algo.recruit.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

//TODO - 페이징
@Getter
public class RecruitPostListResponse {

    private Long totalElements;
    private List<RecruitPostResponse> posts;

    @Builder
    private RecruitPostListResponse(Long totalElements, List<RecruitPostResponse> posts) {
        this.totalElements = totalElements;
        this.posts = posts;
    }
}
