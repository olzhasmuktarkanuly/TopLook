package com.toplook.service;

import com.toplook.dto.UpdateProfileRequest;
import com.toplook.dto.UserDto;
import com.toplook.entity.User;
import com.toplook.repository.FollowRepository;
import com.toplook.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private FollowRepository followRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("alice")
                .email("alice@example.com")
                .password("encoded")
                .bio("Fashion lover")
                .build();
    }

    @Test
    void getProfile_ShouldReturnDto() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(followRepository.countByFollowing(user)).thenReturn(10L);
        when(followRepository.countByFollower(user)).thenReturn(5L);

        UserDto dto = userService.getProfile("alice");

        assertThat(dto.getUsername()).isEqualTo("alice");
        assertThat(dto.getBio()).isEqualTo("Fashion lover");
        assertThat(dto.getFollowersCount()).isEqualTo(10L);
        assertThat(dto.getFollowingCount()).isEqualTo(5L);
    }

    @Test
    void getProfile_ShouldThrow_WhenNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfile("ghost"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void updateProfile_ShouldUpdateBioAndAvatar() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setBio("Updated bio");
        request.setAvatarUrl("http://cdn.example.com/avatar.jpg");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(followRepository.countByFollowing(user)).thenReturn(0L);
        when(followRepository.countByFollower(user)).thenReturn(0L);

        UserDto dto = userService.updateProfile("alice", request);

        assertThat(dto.getBio()).isEqualTo("Updated bio");
        assertThat(dto.getAvatarUrl()).isEqualTo("http://cdn.example.com/avatar.jpg");
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_ShouldNotOverwrite_WhenFieldIsNull() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setBio(null);
        request.setAvatarUrl(null);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(followRepository.countByFollowing(user)).thenReturn(0L);
        when(followRepository.countByFollower(user)).thenReturn(0L);

        UserDto dto = userService.updateProfile("alice", request);

        assertThat(dto.getBio()).isEqualTo("Fashion lover");
    }
}
