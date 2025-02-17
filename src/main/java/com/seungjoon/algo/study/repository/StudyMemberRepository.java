package com.seungjoon.algo.study.repository;

import com.seungjoon.algo.study.domain.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
}
