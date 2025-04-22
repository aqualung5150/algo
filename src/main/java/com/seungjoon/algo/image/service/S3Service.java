package com.seungjoon.algo.image.service;

import com.seungjoon.algo.image.domain.Image;
import com.seungjoon.algo.image.dto.DeleteRequest;
import com.seungjoon.algo.image.dto.ImagesResponse;
import com.seungjoon.algo.image.repository.ImageRepository;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class S3Service implements ImageService {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;
    private final S3Template s3Template;

    @Value("${cloudfront.base-url}")
    private String baseUrl;

    private final ImageRepository imageRepository;

    @Override
    public ImagesResponse upload(List<MultipartFile> multipartFiles) throws IOException {
        return new ImagesResponse(storeFiles(multipartFiles));
    }

    private List<String> storeFiles(List<MultipartFile> multipartFiles) throws IOException {

        List<String> paths = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {

            if (multipartFile.isEmpty()) {
                continue;
            }

            String path = storeFile(multipartFile);

            paths.add(path);
        }

        return paths;
    }

    private String storeFile(MultipartFile multipartFile) throws IOException {

        if (multipartFile.isEmpty()) {
            return null;
        }

        String storeFilename = createStoreFilename(multipartFile);

        s3Template.upload(
                bucket,
                "images/" + storeFilename,
                multipartFile.getInputStream(),
                ObjectMetadata.builder().contentType(multipartFile.getContentType()).build()
        );

        String path = baseUrl + "/images/" + storeFilename;

        //DB 저장
        imageRepository.save(new Image(storeFilename));

        return path;
    }

    private String createStoreFilename(MultipartFile multipartFile) {

        String fileName = multipartFile.getOriginalFilename();
        String ext = extractExt(fileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String fileName) {

        int idx = fileName.lastIndexOf(".");
        if (idx != -1) {
            return fileName.substring(idx + 1);
        }
        return "";
    }

    @Override
    public void delete(DeleteRequest request) {

        for (String filename : request.getImages()) {
            deleteFile(filename);
        }
    }

    private void deleteFile(String filename) {

        s3Template.deleteObject(bucket, "images/" + filename);
    }
}
