package com.seungjoon.algo.image.controller;


import com.seungjoon.algo.image.dto.DeleteRequest;
import com.seungjoon.algo.image.dto.ImagesResponse;
import com.seungjoon.algo.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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

    @ResponseBody
    @DeleteMapping
    public ResponseEntity<Void> delete(
            @RequestBody DeleteRequest request
    ) {
        imageService.delete(request);
        return ResponseEntity.noContent().build();
    }
}
