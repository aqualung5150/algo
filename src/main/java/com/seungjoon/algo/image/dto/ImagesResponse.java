package com.seungjoon.algo.image.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImagesResponse {

    private List<String> images = new ArrayList<>();

    public ImagesResponse(List<String> images) {
        this.images = images;
    }
}
