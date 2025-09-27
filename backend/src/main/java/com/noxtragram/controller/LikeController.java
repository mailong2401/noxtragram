package com.noxtragram.controller;

import com.noxtragram.model.dto.response.LikeResponseDTO;
import com.noxtragram.service.LikeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/likes")
public class LikeController {

  private final LikeService likeService;

  public LikeController(LikeService likeService) {
    this.likeService = likeService;
  }

  // Like một post
  @PostMapping("/posts/{postId}")
  public ResponseEntity<Void> likePost(
      @PathVariable Long postId,
      @RequestHeader("X-User-Id") Long userId) {
    likeService.likePost(postId, userId);
    return ResponseEntity.ok().build();
  }

  // Unlike một post
  @DeleteMapping("/posts/{postId}")
  public ResponseEntity<Void> unlikePost(
      @PathVariable Long postId,
      @RequestHeader("X-User-Id") Long userId) {
    likeService.unlikePost(postId, userId);
    return ResponseEntity.ok().build();
  }

  // Lấy danh sách like của một post
  @GetMapping("/posts/{postId}")
  public ResponseEntity<Page<LikeResponseDTO>> getLikesByPost(
      @PathVariable Long postId,
      @PageableDefault(size = 20) Pageable pageable,
      @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
    Page<LikeResponseDTO> likes = likeService.getLikesByPost(postId, pageable);
    return ResponseEntity.ok(likes);
  }

  // Lấy danh sách post mà user đã like
  @GetMapping("/users/{userId}/posts")
  public ResponseEntity<Page<LikeResponseDTO>> getLikesByUser(
      @PathVariable Long userId,
      @PageableDefault(size = 20) Pageable pageable,
      @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
    Page<LikeResponseDTO> likes = likeService.getLikesByUser(userId, pageable);
    return ResponseEntity.ok(likes);
  }

  // Kiểm tra user đã like post chưa
  @GetMapping("/posts/{postId}/check")
  public ResponseEntity<Boolean> isPostLikedByUser(
      @PathVariable Long postId,
      @RequestHeader("X-User-Id") Long userId) {
    boolean isLiked = likeService.isPostLikedByUser(postId, userId);
    return ResponseEntity.ok(isLiked);
  }

  // Lấy số like của một post
  @GetMapping("/posts/{postId}/count")
  public ResponseEntity<Long> getLikeCount(@PathVariable Long postId) {
    long count = likeService.getLikeCount(postId);
    return ResponseEntity.ok(count);
  }

  // Lấy danh sách user ID đã like post (cho real-time updates)
  @GetMapping("/posts/{postId}/user-ids")
  public ResponseEntity<List<Long>> getUserIdsWhoLikedPost(@PathVariable Long postId) {
    List<Long> userIds = likeService.getUserIdsWhoLikedPost(postId);
    return ResponseEntity.ok(userIds);
  }
}
