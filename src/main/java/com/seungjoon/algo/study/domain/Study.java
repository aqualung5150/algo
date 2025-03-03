package com.seungjoon.algo.study.domain;

import com.seungjoon.algo.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long id;

    private String name;

    private Integer numberOfMembers;

    private LocalDate firstSubmitDate;
    private LocalDate lastSubmitDate;

    @Enumerated(EnumType.STRING)
    private StudyState state;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_rule_id")
    private StudyRule studyRule;

    @OneToMany(mappedBy = "study")
    private List<StudyMember> studyMembers = new ArrayList<>();

    @Builder
    public Study(String name, Integer numberOfMembers, LocalDate firstSubmitDate, LocalDate lastSubmitDate, StudyState state, StudyRule studyRule) {
        this.name = name;
        this.numberOfMembers = numberOfMembers;
        this.firstSubmitDate = firstSubmitDate;
        this.lastSubmitDate = lastSubmitDate;
        this.state = state;
        this.studyRule = studyRule;
    }

    public void addStudyMembers(List<StudyMember> studyMembers) {
        this.studyMembers.addAll(studyMembers);
    }

    public void changeState(StudyState state) {
        this.state = state;
    }

    public void changeNumberOfMembers(int numberOfMembers) {
        this.numberOfMembers = numberOfMembers;
    }
}
