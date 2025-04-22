package com.seungjoon.algo.config;

import com.seungjoon.algo.image.domain.Image;
import com.seungjoon.algo.image.repository.ImageRepository;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.seungjoon.algo.image.domain.ImageType.TEMPORARY;

@Component
@RequiredArgsConstructor
public class ImageCleanupScheduler {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;
    private final S3Template s3Template;

    private final ImageRepository imageRepository;

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void cleanImages() {
        List<Image> unusedImages = imageRepository.findAllByType(TEMPORARY);
        for (Image image : unusedImages) {
            deleteFile(image.getId());
        }
        imageRepository.deleteAll(unusedImages);
    }

    private void deleteFile(String filename) {

        s3Template.deleteObject(bucket, "images/" + filename);
    }
}
