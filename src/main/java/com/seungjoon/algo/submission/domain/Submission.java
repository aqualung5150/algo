package com.seungjoon.algo.submission.domain;

import com.seungjoon.algo.global.BaseEntity;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.study.domain.Study;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Submission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    private Long id;

    private int subjectNumber;
    private String content;

    @Enumerated(EnumType.STRING)
    private SubmissionVisibility visibility;

    @CreatedDate
    private LocalDate submitDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    Study study;

    @OneToMany(mappedBy = "submission")
    private List<SubmissionTag> tags = new ArrayList<>();

    @Builder
    public Submission(int subjectNumber, String content, SubmissionVisibility visibility, Member member, Study study) {
        this.subjectNumber = subjectNumber;
        this.content = content;
        this.visibility = visibility;
        this.member = member;
        this.study = study;
    }

    public void addSubmissionTags(List<SubmissionTag> submissionTags) {
        this.tags.addAll(submissionTags);
    }
}
