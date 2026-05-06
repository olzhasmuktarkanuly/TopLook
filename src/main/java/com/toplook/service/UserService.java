package com.toplook.service;

import com.toplook.dto.UpdateProfileRequest;
import com.toplook.dto.UserDto;
import com.toplook.entity.User;
import com.toplook.repository.FollowRepository;
import com.toplook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public UserDto getProfile(String username) {
        User user = findByUsername(username);
        return toDto(user);
    }

    public UserDto getProfileById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toDto(user);
    }

    @Transactional
    public UserDto updateProfile(String username, UpdateProfileRequest request) {
        User user = findByUsername(username);
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        userRepository.save(user);
        return toDto(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .followersCount(followRepository.countByFollowing(user))
                .followingCount(followRepository.countByFollower(user))
                .build();
    }
}
