package com.toplook.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    private String content;
    private String authorUsername;
    private Long authorId;
    private LocalDateTime createdAt;
}
