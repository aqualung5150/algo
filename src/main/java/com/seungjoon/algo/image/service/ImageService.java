package com.seungjoon.algo.image.service;

import com.seungjoon.algo.image.dto.ImagesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${file.dir}")
    private String fileDir;

    public ImagesResponse upload(List<MultipartFile> multipartFiles) throws IOException {
        return new ImagesResponse(storeFiles(multipartFiles));
    }

    private List<String> storeFiles(List<MultipartFile> multipartFiles) throws IOException {

        List<String> storedFilenames = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {

            if (multipartFile.isEmpty()) {
                continue;
            }

            String storeFilename = storeFile(multipartFile);

            storedFilenames.add(storeFilename);
        }

        return storedFilenames;
    }

    private String storeFile(MultipartFile multipartFile) throws IOException {

        if (multipartFile.isEmpty()) {
            return null;
        }

        String storeFilename = createStoreFilename(multipartFile);
        multipartFile.transferTo(new File(fileDir + storeFilename));

        return storeFilename;
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
