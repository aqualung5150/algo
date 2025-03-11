package com.seungjoon.algo.image.controller;


import com.seungjoon.algo.image.dto.ImagesResponse;
import com.seungjoon.algo.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @ResponseBody
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImagesResponse> upload(@RequestParam("image") List<MultipartFile> multipartFiles) throws IOException {

        ImagesResponse imagesResponse = imageService.upload(multipartFiles);
        return ResponseEntity.ok(imagesResponse);
    }
}
