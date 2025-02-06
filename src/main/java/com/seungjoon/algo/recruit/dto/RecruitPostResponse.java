package com.seungjoon.algo.recruit.dto;

import com.seungjoon.algo.member.dto.AuthorResponse;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.study.dto.StudyRuleResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecruitPostResponse {

    private Long id;
    private AuthorResponse author;
    private String title;
    private String content;
    private StudyRuleResponse studyRule;

    public static RecruitPostResponse from(RecruitPost post) {
        return new RecruitPostResponse(
                post.getId(),
                AuthorResponse.from(post.getMember()),
                post.getTitle(),
                post.getContent(),
                StudyRuleResponse.from(post.getStudyRule())
        );
    }
}
