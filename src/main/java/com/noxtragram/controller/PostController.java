package com.noxtragram.controller;

import com.noxtragram.model.dto.response.*;
import com.noxtragram.model.dto.request.*;
import com.noxtragram.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
  }

  @PostMapping
  public ResponseEntity<PostResponseDTO> createPost(
      @Valid @RequestBody PostRequestDTO postRequest,
      @RequestAttribute Long userId) {

    PostResponseDTO createdPost = postService.createPost(postRequest, userId);
    return ResponseEntity.ok(createdPost);
  }

  @GetMapping("/{postId}")
  public ResponseEntity<PostResponseDTO> getPost(
      @PathVariable Long postId,
      @RequestAttribute Long userId) {

    PostResponseDTO post = postService.getPostById(postId, userId);
    return ResponseEntity.ok(post);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<Page<PostResponseDTO>> getUserPosts(
      @PathVariable Long userId,
      @RequestAttribute Long currentUserId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<PostResponseDTO> posts = postService.getPostsByUserId(userId, currentUserId, pageable);
    return ResponseEntity.ok(posts);
  }

  @GetMapping("/feed")
  public ResponseEntity<Page<PostResponseDTO>> getFeed(
      @RequestAttribute Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<PostResponseDTO> posts = postService.getFeedPosts(userId, pageable);
    return ResponseEntity.ok(posts);
  }

  @GetMapping("/hashtag/{hashtag}")
  public ResponseEntity<Page<PostResponseDTO>> getPostsByHashtag(
      @PathVariable String hashtag,
      @RequestAttribute Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<PostResponseDTO> posts = postService.getPostsByHashtag(hashtag, userId, pageable);
    return ResponseEntity.ok(posts);
  }

  @GetMapping("/saved")
  public ResponseEntity<Page<PostResponseDTO>> getSavedPosts(
      @RequestAttribute Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<PostResponseDTO> posts = postService.getSavedPosts(userId, userId, pageable);
    return ResponseEntity.ok(posts);
  }

  @GetMapping("/popular")
  public ResponseEntity<Page<PostResponseDTO>> getPopularPosts(
      @RequestAttribute Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("likeCount").descending());
    Page<PostResponseDTO> posts = postService.getPopularPosts(userId, pageable);
    return ResponseEntity.ok(posts);
  }

  @PutMapping("/{postId}")
  public ResponseEntity<PostResponseDTO> updatePost(
      @PathVariable Long postId,
      @Valid @RequestBody PostRequestDTO postRequest,
      @RequestAttribute Long userId) {

    PostResponseDTO updatedPost = postService.updatePost(postId, postRequest, userId);
    return ResponseEntity.ok(updatedPost);
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<Map<String, String>> deletePost(
      @PathVariable Long postId,
      @RequestAttribute Long userId) {

    postService.deletePost(postId, userId);

    Map<String, String> response = new HashMap<>();
    response.put("message", "Post deleted successfully");
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{postId}/like")
  public ResponseEntity<Map<String, String>> likePost(
      @PathVariable Long postId,
      @RequestAttribute Long userId) {

    postService.likePost(postId, userId);

    Map<String, String> response = new HashMap<>();
    response.put("message", "Post liked successfully");
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{postId}/like")
  public ResponseEntity<Map<String, String>> unlikePost(
      @PathVariable Long postId,
      @RequestAttribute Long userId) {

    postService.unlikePost(postId, userId);

    Map<String, String> response = new HashMap<>();
    response.put("message", "Post unliked successfully");
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{postId}/save")
  public ResponseEntity<Map<String, String>> savePost(
      @PathVariable Long postId,
      @RequestAttribute Long userId) {

    postService.savePost(postId, userId);

    Map<String, String> response = new HashMap<>();
    response.put("message", "Post saved successfully");
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{postId}/save")
  public ResponseEntity<Map<String, String>> unsavePost(
      @PathVariable Long postId,
      @RequestAttribute Long userId) {

    postService.unsavePost(postId, userId);

    Map<String, String> response = new HashMap<>();
    response.put("message", "Post unsaved successfully");
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{postId}/interactions")
  public ResponseEntity<Map<String, Boolean>> getPostInteractions(
      @PathVariable Long postId,
      @RequestAttribute Long userId) {

    boolean isLiked = postService.isPostLikedByUser(postId, userId);
    boolean isSaved = postService.isPostSavedByUser(postId, userId);

    Map<String, Boolean> interactions = new HashMap<>();
    interactions.put("isLiked", isLiked);
    interactions.put("isSaved", isSaved);

    return ResponseEntity.ok(interactions);
  }
}
