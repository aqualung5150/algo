package com.seungjoon.algo.study.domain;

import com.seungjoon.algo.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_rule_id")
    private Long id;

    private int numberOfMembers;
    private int totalWeek;
    private DayOfWeek submitDayOfWeek;
    private int submitPerWeek;

    @OneToMany(mappedBy = "studyRule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyRuleTag> studyRuleTags = new ArrayList<>();

    @Builder
    private StudyRule(int numberOfMembers, int totalWeek, DayOfWeek submitDayOfWeek, int submitPerWeek) {
        this.numberOfMembers = numberOfMembers;
        this.totalWeek = totalWeek;
        this.submitDayOfWeek = submitDayOfWeek;
        this.submitPerWeek = submitPerWeek;
    }

    public void addStudyRuleTags(List<StudyRuleTag> studyRuleTags) {
        this.studyRuleTags.addAll(studyRuleTags);
    }
}
