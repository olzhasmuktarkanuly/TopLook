package com.toplook.service;

import com.toplook.dto.CommentDto;
import com.toplook.dto.CreateCommentRequest;
import com.toplook.entity.*;
import com.toplook.repository.CommentRepository;
import com.toplook.repository.FollowRepository;
import com.toplook.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;
    private final PostService postService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional
    public void likePost(Long postId, String username) {
        User user = userService.findByUsername(username);
        Post post = postService.getPostEntity(postId);

        if (likeRepository.existsByUserAndPost(user, post)) {
            throw new IllegalStateException("Already liked");
        }

        likeRepository.save(Like.builder().user(user).post(post).build());
        notificationService.sendLikeNotification(post.getAuthor().getUsername(), username);
    }

    @Transactional
    public void unlikePost(Long postId, String username) {
        User user = userService.findByUsername(username);
        Post post = postService.getPostEntity(postId);

        Like like = likeRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new IllegalStateException("Not liked yet"));
        likeRepository.delete(like);
    }

    @Transactional
    public CommentDto addComment(Long postId, String username, CreateCommentRequest request) {
        User user = userService.findByUsername(username);
        Post post = postService.getPostEntity(postId);

        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .post(post)
                .build();
        commentRepository.save(comment);
        notificationService.sendCommentNotification(post.getAuthor().getUsername(), username);

        return toCommentDto(comment);
    }

    public List<CommentDto> getComments(Long postId) {
        Post post = postService.getPostEntity(postId);
        return commentRepository.findByPostOrderByCreatedAtDesc(post)
                .stream().map(this::toCommentDto).toList();
    }

    @Transactional
    public void follow(Long targetId, String username) {
        User follower = userService.findByUsername(username);
        User following = userService.findByUsername(
                userService.getProfileById(targetId).getUsername()
        );

        if (follower.getId().equals(following.getId())) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new IllegalStateException("Already following");
        }

        followRepository.save(Follow.builder().follower(follower).following(following).build());
        notificationService.sendFollowNotification(following.getUsername(), username);
    }

    @Transactional
    public void unfollow(Long targetId, String username) {
        User follower = userService.findByUsername(username);
        User following = userService.findByUsername(
                userService.getProfileById(targetId).getUsername()
        );

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new IllegalStateException("Not following"));
        followRepository.delete(follow);
    }

    private CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorUsername(comment.getUser().getUsername())
                .authorId(comment.getUser().getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
