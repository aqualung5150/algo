package com.seungjoon.algo.image.repository;

import com.seungjoon.algo.image.domain.Image;
import com.seungjoon.algo.recruit.domain.RecruitPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, String> {

    List<Image> findAllByRecruitPost(RecruitPost recruitPost);
}
