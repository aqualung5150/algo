package com.seungjoon.algo.submission.domain;

import com.seungjoon.algo.global.BaseEntity;
import com.seungjoon.algo.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Evaluation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id")
    private Long id;

    private String content;

    @Enumerated(EnumType.STRING)
    private PassFail passFail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private Submission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private Evaluation(String content, PassFail passFail, Submission submission, Member member) {
        this.content = content;
        this.passFail = passFail;
        this.submission = submission;
        this.member = member;
    }

    public void changeEvaluation(String content, String passFail) {
        this.content = content;
        this.passFail = PassFail.valueOf(passFail);
    }
}
