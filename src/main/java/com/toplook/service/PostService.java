package com.toplook.service;

import com.toplook.dto.CreatePostRequest;
import com.toplook.dto.PostDto;
import com.toplook.entity.Post;
import com.toplook.entity.User;
import com.toplook.repository.LikeRepository;
import com.toplook.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;

    @Transactional
    public PostDto createPost(String username, CreatePostRequest request) {
        User author = userService.findByUsername(username);
        String category = normalizeCategory(request.getCategory());
        Post post = Post.builder()
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .category(category)
                .author(author)
                .build();
        postRepository.save(post);
        return toDto(post);
    }

    public Page<PostDto> getAllPosts(int page, int size) {
        return postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(this::toDto);
    }

    public Page<PostDto> getAllPosts(String category, int page, int size) {
        if (category == null || category.isBlank() || "All".equalsIgnoreCase(category)) {
            return getAllPosts(page, size);
        }
        return postRepository.findByCategoryIgnoreCaseOrderByCreatedAtDesc(category.trim(), PageRequest.of(page, size))
                .map(this::toDto);
    }

    public List<String> getCategories() {
        return postRepository.findDistinctCategories();
    }

    public Page<PostDto> getFeed(String username, int page, int size) {
        User user = userService.findByUsername(username);
        return postRepository.findFeedForUser(user, PageRequest.of(page, size))
                .map(this::toDto);
    }

    public List<PostDto> getUserPosts(String username) {
        User user = userService.findByUsername(username);
        return postRepository.findByAuthorOrderByCreatedAtDesc(user)
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public void deletePost(Long postId, String username) {
        Post post = getPostEntity(postId);
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new SecurityException("Not authorized to delete this post");
        }
        postRepository.delete(post);
    }

    public Post getPostEntity(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
    }

    private PostDto toDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .description(post.getDescription())
                .imageUrl(post.getImageUrl())
                .category(post.getCategory())
                .authorUsername(post.getAuthor().getUsername())
                .authorId(post.getAuthor().getId())
                .likesCount(likeRepository.countByPost(post))
                // For a simple DTO we expose the flag as false here; the UI toggles it immediately after user action.
                .liked(false)
                .commentsCount(post.getComments().size())
                .createdAt(post.getCreatedAt())
                .build();
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "General";
        }
        return category.trim();
    }
}

