package com.seungjoon.algo.recruit.domain;

import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.study.domain.StudyRule;
import jakarta.persistence.*;

@Entity
public class RecruitPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_rule_id")
    private StudyRule studyRule;

    private String title;
    private String content;
}
