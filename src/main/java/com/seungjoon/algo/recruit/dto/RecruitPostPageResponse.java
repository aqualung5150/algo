package com.seungjoon.algo.recruit.dto;

import com.seungjoon.algo.recruit.domain.RecruitPost;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

//TODO: 게시글의 제목(메타데이터)정도만 필요한 경우 List<RecruitPostResponse> 부분 수정해야함.
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecruitPostPageResponse {

    private Long totalCount;
    private List<RecruitPostResponse> posts;

    public static RecruitPostPageResponse of(Long totalCount, List<RecruitPost> posts) {
        return new RecruitPostPageResponse(
                totalCount,
                posts.stream().map(RecruitPostResponse::from).toList()
        );
    }
}
