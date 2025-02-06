package com.seungjoon.algo.study.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_rule_id")
    private Long id;

    private int totalWeek;
    private DayOfWeek submitDayOfWeek;
    private int submitPerWeek;

    @Builder
    private StudyRule(int totalWeek, DayOfWeek submitDayOfWeek, int submitPerWeek) {
        this.totalWeek = totalWeek;
        this.submitDayOfWeek = submitDayOfWeek;
        this.submitPerWeek = submitPerWeek;
    }
}
