package com.seungjoon.algo.study.domain;

import com.seungjoon.algo.global.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Study extends BaseEntity {

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
