package com.seungjoon.algo.subject.domain;

import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.study.domain.Study;
import jakarta.persistence.*;

@Entity
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    private Long id;

    private int subjectNumber;
    private String content;

    @Enumerated(EnumType.STRING)
    private SubmissionVisibility visibility;

    private int weekNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    Study study;
}
