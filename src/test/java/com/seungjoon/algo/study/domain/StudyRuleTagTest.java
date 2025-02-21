package com.seungjoon.algo.study.domain;

import com.seungjoon.algo.subject.domain.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class StudyRuleTagTest {

    @Test
    void updateList() {
        //given

        StudyRule studyRule = BDDMockito.mock(StudyRule.class);
        Tag tag1 = new Tag(1L, "dp");
        Tag tag2 = new Tag(2L, "dfs");
        Tag tag3 = new Tag(3L, "bfs");

        List<StudyRuleTag> studyRuleTags = StudyRuleTag.toListFromTags(studyRule, List.of(tag1, tag2));

        studyRule.addStudyRuleTags(studyRuleTags);

        //when
        List<StudyRuleTag> savedStudyRuleTags = studyRule.getStudyRuleTags();
        StudyRuleTag.updateListFromTags(studyRule, savedStudyRuleTags, List.of(tag2, tag3));

        //then
        assertThat(savedStudyRuleTags).hasSize(2);
        List<Tag> savedTags = savedStudyRuleTags.stream().map(StudyRuleTag::getTag).toList();
        assertThat(savedTags).hasSize(2);
        assertThat(savedTags).containsExactly(tag2, tag3);
    }
}