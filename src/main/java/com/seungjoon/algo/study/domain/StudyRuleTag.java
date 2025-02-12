package com.seungjoon.algo.study.domain;

import com.seungjoon.algo.subject.domain.Tag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyRuleTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_rule_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_rule_id")
    private StudyRule studyRule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    private StudyRuleTag(StudyRule studyRule, Tag tag) {
        this.studyRule = studyRule;
        this.tag = tag;
    }

    public static List<StudyRuleTag> toListFromTags(StudyRule studyRule, List<Tag> tags) {
        return tags.stream()
                .map(tag -> new StudyRuleTag(studyRule, tag))
                .toList();
    }
}
