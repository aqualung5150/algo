package com.seungjoon.algo.recruit.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

//TODO: 게시글의 제목(메타데이터)정도만 필요한 경우 List<RecruitPostResponse> 부분 수정해야함.
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecruitPostSliceResponse {

    private Boolean hasNext;
    private List<RecruitPostResponse> posts;

    public static RecruitPostSliceResponse of(Boolean hasNext, List<RecruitPostResponse> posts) {
        return new RecruitPostSliceResponse(hasNext, posts);
    }
}
