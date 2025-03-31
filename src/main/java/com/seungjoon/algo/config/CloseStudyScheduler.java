package com.seungjoon.algo.config;

import com.seungjoon.algo.study.domain.Study;
import com.seungjoon.algo.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.seungjoon.algo.study.domain.StudyState.COMPLETED;

@Slf4j
@Component
@RequiredArgsConstructor
public class CloseStudyScheduler {

    private final StudyRepository studyRepository;

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void closeStudy() {
        List<Study> result = studyRepository.findByLastSubmitDateBefore(LocalDate.now());

        result.forEach(study -> {
            log.info("study {} is closed", study.getId());
            study.changeState(COMPLETED);
        });
    }
}
