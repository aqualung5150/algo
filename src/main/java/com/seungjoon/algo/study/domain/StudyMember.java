package com.seungjoon.algo.study.domain;

import com.seungjoon.algo.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_member_id")
    private Long id;

    private Integer notSubmitted = 0;

    @Enumerated(EnumType.STRING)
    private StudyMemberRole role;

    @Enumerated(EnumType.STRING)
    private StudyMemberState state = StudyMemberState.ACTIVE;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private StudyMember(StudyMemberRole role, Study study, Member member) {
        this.role = role;
        this.study = study;
        this.member = member;
    }
}
