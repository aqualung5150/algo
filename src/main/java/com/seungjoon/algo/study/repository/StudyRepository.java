package com.seungjoon.algo.study.repository;

import com.seungjoon.algo.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long> {
}
