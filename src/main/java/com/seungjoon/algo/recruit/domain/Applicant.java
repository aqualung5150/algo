package com.seungjoon.algo.recruit.domain;

import com.seungjoon.algo.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@IdClass(ApplicantId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Applicant {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_post_id")
    private RecruitPost recruitPost;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private Applicant(RecruitPost recruitPost, Member member) {
        this.recruitPost = recruitPost;
        this.member = member;
    }
}
