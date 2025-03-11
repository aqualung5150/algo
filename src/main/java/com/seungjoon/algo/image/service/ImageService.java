package com.seungjoon.algo.image.service;

import com.seungjoon.algo.image.dto.DeleteRequest;
import com.seungjoon.algo.image.dto.ImagesResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    ImagesResponse upload(List<MultipartFile> multipartFiles) throws IOException;

    void delete(DeleteRequest request);
}
