package com.toplook.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostDto {
    private Long id;
    private String description;
    private String imageUrl;
    private String category;
    private String authorUsername;
    private Long authorId;
    private long likesCount;
    private boolean liked;
    private int commentsCount;
    private LocalDateTime createdAt;
}
