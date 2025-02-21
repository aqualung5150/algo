package com.seungjoon.algo.recruit.dto;

import com.seungjoon.algo.member.dto.ProfileResponse;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.recruit.domain.RecruitPostState;
import com.seungjoon.algo.study.dto.StudyRuleResponse;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class RecruitPostResponse {

    private Long id;
    private ProfileResponse author;
    private String title;
    private String content;
    private String state;
    private StudyRuleResponse studyRule;

    public static RecruitPostResponse from(RecruitPost post) {
        return new RecruitPostResponse(
                post.getId(),
                ProfileResponse.from(post.getMember()),
                post.getTitle(),
                post.getContent(),
                post.getState().name(),
                StudyRuleResponse.from(post.getStudyRule())
        );
    }
}
