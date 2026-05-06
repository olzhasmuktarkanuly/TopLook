package com.toplook.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostRequest {

    @NotBlank
    @Size(max = 500)
    private String description;

    private String imageUrl;

    @Size(max = 40)
    private String category;
}

