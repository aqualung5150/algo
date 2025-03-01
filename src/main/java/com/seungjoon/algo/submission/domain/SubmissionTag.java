package com.seungjoon.algo.submission.domain;

import com.seungjoon.algo.global.BaseEntity;
import com.seungjoon.algo.study.domain.StudyRule;
import com.seungjoon.algo.study.domain.StudyRuleTag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubmissionTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private Submission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    private SubmissionTag(Submission submission, Tag tag) {
        this.submission = submission;
        this.tag = tag;
    }

    public static List<SubmissionTag> toListFromTags(Submission submission, List<Tag> tags) {
        return tags.stream()
                .map(tag -> new SubmissionTag(submission, tag))
                .toList();
    }
}
