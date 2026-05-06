package com.toplook.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String bio;
    private String avatarUrl;
    private long followersCount;
    private long followingCount;
}
