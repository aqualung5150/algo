package com.seungjoon.algo.image.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DeleteRequest {

    @NotNull
    List<String> filenames;
}
