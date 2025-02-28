package com.seungjoon.algo.evaluation.domain;

import com.seungjoon.algo.global.BaseEntity;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.submission.domain.Submission;
import jakarta.persistence.*;

@Entity
public class Evaluation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private Submission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String content;

    @Enumerated(EnumType.STRING)
    private PassFail passFail;
}
