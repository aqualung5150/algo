package com.seungjoon.algo.study.repository;

import com.seungjoon.algo.study.domain.Study;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

@DataJpaTest
class StudyRepositoryTest {

    @Autowired
    private StudyRepository studyRepository;

    @Test
    void findByLastModifiedByBefore() {
        //given
        Study before = Study.builder()
                .lastSubmitDate(LocalDate.now().minusDays(1))
                .build();
        studyRepository.save(before);

        Study after = Study.builder()
                .lastSubmitDate(LocalDate.now().plusDays(1))
                .build();
        studyRepository.save(after);

        Study equal = Study.builder()
                .lastSubmitDate(LocalDate.now())
                .build();
        studyRepository.save(equal);

        //when
        List<Study> result = studyRepository.findByLastSubmitDateBefore(LocalDate.now());

        //then
        Assertions.assertThat(result).hasSize(1);
    }
}