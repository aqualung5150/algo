package com.seungjoon.algo.study.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long id;

    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime deadline;
    private int weekNumber;

    @Enumerated(EnumType.STRING)
    private StudyState state;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_rule_id")
    private StudyRule studyRule;
}
