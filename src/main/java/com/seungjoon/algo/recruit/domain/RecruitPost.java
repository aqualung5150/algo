package com.seungjoon.algo.recruit.domain;

import com.seungjoon.algo.global.BaseEntity;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.study.domain.StudyRule;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.seungjoon.algo.recruit.domain.RecruitPostState.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private RecruitPostState state = RECRUITING;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_rule_id")
    private StudyRule studyRule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public RecruitPost(String title, String content, StudyRule studyRule, Member member) {
        this.title = title;
        this.content = content;
        this.studyRule = studyRule;
        this.member = member;
    }

    public void changeRecruitPost(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void changeRecruitPostState(RecruitPostState state) {
        this.state = state;
    }
}
