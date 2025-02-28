package com.seungjoon.algo.submission.repository;

import com.seungjoon.algo.submission.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByIdIn(List<Long> ids);
}
