package com.toplook.service;

import com.toplook.dto.CreatePostRequest;
import com.toplook.dto.PostDto;
import com.toplook.entity.Post;
import com.toplook.entity.User;
import com.toplook.repository.LikeRepository;
import com.toplook.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private PostService postService;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encoded")
                .build();

        post = Post.builder()
                .id(1L)
                .description("Cool outfit!")
                .imageUrl("http://example.com/img.jpg")
                .author(user)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createPost_ShouldReturnDto_WhenValid() {
        CreatePostRequest request = new CreatePostRequest();
        request.setDescription("Cool outfit!");
        request.setImageUrl("http://example.com/img.jpg");

        when(userService.findByUsername("testuser")).thenReturn(user);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(likeRepository.countByPost(any())).thenReturn(0L);

        PostDto dto = postService.createPost("testuser", request);

        assertThat(dto.getDescription()).isEqualTo("Cool outfit!");
        assertThat(dto.getAuthorUsername()).isEqualTo("testuser");
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void deletePost_ShouldDelete_WhenOwner() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L, "testuser");

        verify(postRepository).delete(post);
    }

    @Test
    void deletePost_ShouldThrow_WhenNotOwner() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.deletePost(1L, "otheruser"))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Not authorized");

        verify(postRepository, never()).delete(any());
    }

    @Test
    void getAllPosts_ShouldReturnPage() {
        Page<Post> postPage = new PageImpl<>(List.of(post));
        when(postRepository.findAllByOrderByCreatedAtDesc(any(PageRequest.class))).thenReturn(postPage);
        when(likeRepository.countByPost(any())).thenReturn(2L);

        Page<PostDto> result = postService.getAllPosts(0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLikesCount()).isEqualTo(2L);
    }

    @Test
    void getPostEntity_ShouldThrow_WhenNotFound() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPostEntity(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Post not found");
    }
}
