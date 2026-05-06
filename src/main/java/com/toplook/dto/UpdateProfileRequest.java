package com.toplook.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(max = 300)
    private String bio;

    private String avatarUrl;
}
