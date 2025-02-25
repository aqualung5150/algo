package com.seungjoon.algo.study.dto;

import com.seungjoon.algo.study.domain.Study;
import com.seungjoon.algo.study.domain.StudyState;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyPageResponse {

    private long totalCount;
    private List<StudyProfile> studyProfiles;

    public static StudyPageResponse of(long totalCount, List<Study> studies) {

        return new StudyPageResponse(
                totalCount,
                studies.stream().map(study -> new StudyProfile(
                        study.getId(),
                                study.getName(),
                        study.getState())
                        ).toList()
        );
    }

    static public class StudyProfile {
        private Long id;
        private String name;
        private String state;

        public StudyProfile(Long id, String name, StudyState state) {
            this.id = id;
            this.name = name;
            this.state = state.name();
        }
    }
}
