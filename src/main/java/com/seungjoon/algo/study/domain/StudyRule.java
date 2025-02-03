package com.seungjoon.algo.study.domain;

import jakarta.persistence.*;

import java.time.DayOfWeek;

@Entity
public class StudyRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_rule_id")
    private Long id;

    private int totalWeek;
    private DayOfWeek submitDayOfWeek;
    private int submitPerWeek;
}
