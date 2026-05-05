package com.toplook.service;

import com.toplook.dto.CommentDto;
import com.toplook.dto.CreateCommentRequest;
import com.toplook.dto.UserDto;
import com.toplook.entity.*;
import com.toplook.repository.CommentRepository;
import com.toplook.repository.FollowRepository;
import com.toplook.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocialServiceTest {

    @Mock private LikeRepository likeRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private FollowRepository followRepository;
    @Mock private PostService postService;
    @Mock private UserService userService;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private SocialService socialService;

    private User user;
    private User author;
    private Post post;

    @BeforeEach
    void setUp() {
        user   = User.builder().id(1L).username("user1").email("u1@test.com").password("p").build();
        author = User.builder().id(2L).username("author").email("a@test.com").password("p").build();
        post   = Post.builder().id(1L).description("Nice fit").author(author).createdAt(LocalDateTime.now()).build();
    }

    @Test
    void likePost_ShouldSaveLike_WhenNotAlreadyLiked() {
        when(userService.findByUsername("user1")).thenReturn(user);
        when(postService.getPostEntity(1L)).thenReturn(post);
        when(likeRepository.existsByUserAndPost(user, post)).thenReturn(false);

        socialService.likePost(1L, "user1");

        verify(likeRepository).save(any(Like.class));
        verify(notificationService).sendLikeNotification("author", "user1");
    }

    @Test
    void likePost_ShouldThrow_WhenAlreadyLiked() {
        when(userService.findByUsername("user1")).thenReturn(user);
        when(postService.getPostEntity(1L)).thenReturn(post);
        when(likeRepository.existsByUserAndPost(user, post)).thenReturn(true);

        assertThatThrownBy(() -> socialService.likePost(1L, "user1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Already liked");
    }

    @Test
    void unlikePost_ShouldDelete_WhenLiked() {
        Like like = Like.builder().id(1L).user(user).post(post).build();
        when(userService.findByUsername("user1")).thenReturn(user);
        when(postService.getPostEntity(1L)).thenReturn(post);
        when(likeRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(like));

        socialService.unlikePost(1L, "user1");

        verify(likeRepository).delete(like);
    }

    @Test
    void addComment_ShouldReturnCommentDto() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("Great look!");

        Comment saved = Comment.builder()
                .id(1L).content("Great look!").user(user).post(post).createdAt(LocalDateTime.now()).build();

        when(userService.findByUsername("user1")).thenReturn(user);
        when(postService.getPostEntity(1L)).thenReturn(post);
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        CommentDto dto = socialService.addComment(1L, "user1", request);

        assertThat(dto.getContent()).isEqualTo("Great look!");
        assertThat(dto.getAuthorUsername()).isEqualTo("user1");
        verify(notificationService).sendCommentNotification("author", "user1");
    }

    @Test
    void follow_ShouldThrow_WhenAlreadyFollowing() {
        UserDto userDto = UserDto.builder().id(2L).username("author").build();

        when(userService.findByUsername("user1")).thenReturn(user);
        when(userService.getProfileById(2L)).thenReturn(userDto);
        when(userService.findByUsername("author")).thenReturn(author);
        when(followRepository.existsByFollowerAndFollowing(user, author)).thenReturn(true);

        assertThatThrownBy(() -> socialService.follow(2L, "user1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Already following");
    }

    @Test
    void follow_ShouldThrow_WhenFollowingSelf() {
        UserDto selfDto = UserDto.builder().id(1L).username("user1").build();

        when(userService.findByUsername("user1")).thenReturn(user);
        when(userService.getProfileById(1L)).thenReturn(selfDto);
        when(userService.findByUsername("user1")).thenReturn(user);

        assertThatThrownBy(() -> socialService.follow(1L, "user1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot follow yourself");
    }
}
