package com.seungjoon.algo.study.repository;

import com.seungjoon.algo.study.domain.StudyRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRuleRepository extends JpaRepository<StudyRule, Long> {
}
