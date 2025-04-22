package com.seungjoon.algo.image.service;

import com.seungjoon.algo.image.domain.Image;
import com.seungjoon.algo.image.dto.DeleteRequest;
import com.seungjoon.algo.image.dto.ImagesResponse;
import com.seungjoon.algo.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

//@Service
@RequiredArgsConstructor
@Transactional
public class ImageServiceImpl implements ImageService {

    @Value("${file.dir}")
    private String fileDir;

    private final ImageRepository imageRepository;

    @Override
    public ImagesResponse upload(List<MultipartFile> multipartFiles) throws IOException {
        return new ImagesResponse(storeFiles(multipartFiles));
    }

    @Override
    public void delete(DeleteRequest request) {
        //delete
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

        String path = fileDir + "\\" + storeFilename;

        multipartFile.transferTo(new File(path));

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
}
