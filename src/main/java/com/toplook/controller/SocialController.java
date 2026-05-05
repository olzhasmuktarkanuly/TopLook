package com.toplook.controller;

import com.toplook.dto.CommentDto;
import com.toplook.dto.CreateCommentRequest;
import com.toplook.service.SocialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Social")
public class SocialController {

    private final SocialService socialService;

    @PostMapping("/posts/{postId}/like")
    @Operation(summary = "Like a post")
    public ResponseEntity<Void> likePost(@PathVariable Long postId,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        socialService.likePost(postId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{postId}/like")
    @Operation(summary = "Unlike a post")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        socialService.unlikePost(postId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/posts/{postId}/comments")
    @Operation(summary = "Comment on a post")
    public ResponseEntity<CommentDto> addComment(@PathVariable Long postId,
                                                  @AuthenticationPrincipal UserDetails userDetails,
                                                  @Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.ok(socialService.addComment(postId, userDetails.getUsername(), request));
    }

    @GetMapping("/posts/{postId}/comments")
    @Operation(summary = "Get comments for a post")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(socialService.getComments(postId));
    }

    @PostMapping("/users/{userId}/follow")
    @Operation(summary = "Follow a user")
    public ResponseEntity<Void> followUser(@PathVariable Long userId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        socialService.follow(userId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{userId}/follow")
    @Operation(summary = "Unfollow a user")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long userId,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        socialService.unfollow(userId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
