package com.toplook.controller;

import com.toplook.dto.CreatePostRequest;
import com.toplook.dto.PostDto;
import com.toplook.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "Create a new post")
    public ResponseEntity<PostDto> createPost(@AuthenticationPrincipal UserDetails userDetails,
                                               @Valid @RequestBody CreatePostRequest request) {
        return ResponseEntity.ok(postService.createPost(userDetails.getUsername(), request));
    }

    @GetMapping
    @Operation(summary = "Get all posts (paginated)")
    public ResponseEntity<Page<PostDto>> getAllPosts(@RequestParam(required = false) String category,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getAllPosts(category, page, size));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all post categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(postService.getCategories());
    }

    @GetMapping("/feed")
    @Operation(summary = "Get personalized feed")
    public ResponseEntity<Page<PostDto>> getFeed(@AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getFeed(userDetails.getUsername(), page, size));
    }

    @GetMapping("/user/{username}")
    @Operation(summary = "Get posts by username")
    public ResponseEntity<List<PostDto>> getUserPosts(@PathVariable String username) {
        return ResponseEntity.ok(postService.getUserPosts(username));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete own post")
    public ResponseEntity<Void> deletePost(@PathVariable Long id,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
