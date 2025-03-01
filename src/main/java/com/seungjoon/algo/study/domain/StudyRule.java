package com.seungjoon.algo.study.domain;

import com.seungjoon.algo.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.DayOfWeek;
import java.util.ArrayList;
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
    private int minLevel;
    private int maxLevel;
    private int totalWeek;
    @Enumerated(EnumType.STRING)
    private DayOfWeek submitDayOfWeek;
    private int submitPerWeek;

    @OneToMany(mappedBy = "studyRule", cascade = CascadeType.ALL, orphanRemoval = true)
    // OnDelete를 사용하는게 옳은지...cascade 옵션을 빼고 수동으로 삭제할지
    // TODO: JDBC로 벌크인서트를 구현해야 할까
    //https://velog.io/@ttomy/batch-insert
    //https://twosky.tistory.com/62
    //https://medium.com/@hee98.09.14/jpa-id%EC%A0%84%EB%9E%B5%EC%9D%B4-identity%EC%9D%B8-%EC%83%81%ED%83%9C%EC%97%90%EC%84%9C-bulk-insert-%EC%88%98%ED%96%89%ED%95%98%EA%B8%B0-8bf9c760bd82
    //https://sandcastle.tistory.com/98
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<StudyRuleTag> studyRuleTags = new ArrayList<>();

    @Builder
    private StudyRule(int numberOfMembers, int minLevel, int maxLevel, int totalWeek, DayOfWeek submitDayOfWeek, int submitPerWeek) {
        this.numberOfMembers = numberOfMembers;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.totalWeek = totalWeek;
        this.submitDayOfWeek = submitDayOfWeek;
        this.submitPerWeek = submitPerWeek;
    }

    public void addStudyRuleTags(List<StudyRuleTag> studyRuleTags) {
        this.studyRuleTags.addAll(studyRuleTags);
    }

    public void changeStudyRule(
            int numberOfMembers,
            int minLevel,
            int maxLevel,
            int totalWeek,
            DayOfWeek submitDayOfWeek,
            int submitPerWeek
    ) {
        this.numberOfMembers = numberOfMembers;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.totalWeek = totalWeek;
        this.submitDayOfWeek = submitDayOfWeek;
        this.submitPerWeek = submitPerWeek;
    }
}
