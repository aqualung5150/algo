package com.seungjoon.algo.image.domain;

import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import com.seungjoon.algo.submission.domain.Submission;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Image {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private ImageType type = ImageType.TEMPORARY;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_post_id")
    private RecruitPost recruitPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private Submission submission;


    public Image(String id) {
        this.id = id;
    }

    public void changeType(ImageType type) {
        this.type = type;
    }

    public void changeMember(Member member) {
        this.member = member;
    }

    public void changeRecruitPost(RecruitPost recruitPost) {
        this.recruitPost = recruitPost;
    }

    public void changeSubmission(Submission submission) {
        this.submission = submission;
    }
}
