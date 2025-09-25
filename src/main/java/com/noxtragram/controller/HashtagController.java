package com.noxtragram.controller;

import com.noxtragram.model.dto.response.*;
import com.noxtragram.model.dto.TrendingHashtagDTO;
import com.noxtragram.service.HashtagService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hashtags")
public class HashtagController {

  private final HashtagService hashtagService;

  public HashtagController(HashtagService hashtagService) {
    this.hashtagService = hashtagService;
  }

  @GetMapping("/{name}")
  public ResponseEntity<HashtagResponseDTO> getHashtag(
      @PathVariable String name,
      @RequestAttribute(required = false) Long userId) {

    HashtagResponseDTO hashtag = hashtagService.getHashtagByName(name, userId);
    return ResponseEntity.ok(hashtag);
  }

  @PostMapping
  public ResponseEntity<HashtagResponseDTO> createHashtag(
      @Valid @RequestBody Map<String, String> request) {

    String name = request.get("name");
    HashtagResponseDTO hashtag = hashtagService.createHashtag(name);
    return ResponseEntity.ok(hashtag);
  }

  @GetMapping("/search")
  public ResponseEntity<Page<HashtagResponseDTO>> searchHashtags(
      @RequestParam String q,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("postCount").descending());
    Page<HashtagResponseDTO> hashtags = hashtagService.searchHashtags(q, pageable);
    return ResponseEntity.ok(hashtags);
  }

  @GetMapping("/popular")
  public ResponseEntity<Page<HashtagResponseDTO>> getPopularHashtags(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("postCount").descending());
    Page<HashtagResponseDTO> hashtags = hashtagService.getPopularHashtags(pageable);
    return ResponseEntity.ok(hashtags);
  }

  @GetMapping("/recent")
  public ResponseEntity<Page<HashtagResponseDTO>> getRecentHashtags(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<HashtagResponseDTO> hashtags = hashtagService.getRecentHashtags(pageable);
    return ResponseEntity.ok(hashtags);
  }

  @GetMapping("/trending")
  public ResponseEntity<List<TrendingHashtagDTO>> getTrendingHashtags(
      @RequestParam(defaultValue = "10") int limit) {

    List<TrendingHashtagDTO> trendingHashtags = hashtagService.getTrendingHashtags(limit);
    return ResponseEntity.ok(trendingHashtags);
  }

  @GetMapping("/{name}/posts")
  public ResponseEntity<Page<PostResponseDTO>> getPostsByHashtag(
      @PathVariable String name,
      @RequestAttribute Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<PostResponseDTO> posts = hashtagService.getPostsByHashtag(name, userId, pageable);
    return ResponseEntity.ok(posts);
  }

  @PostMapping("/{name}/follow")
  public ResponseEntity<Map<String, String>> followHashtag(
      @PathVariable String name,
      @RequestAttribute Long userId) {

    hashtagService.followHashtag(name, userId);

    Map<String, String> response = new HashMap<>();
    response.put("message", "Hashtag followed successfully");
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{name}/follow")
  public ResponseEntity<Map<String, String>> unfollowHashtag(
      @PathVariable String name,
      @RequestAttribute Long userId) {

    hashtagService.unfollowHashtag(name, userId);

    Map<String, String> response = new HashMap<>();
    response.put("message", "Hashtag unfollowed successfully");
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{name}/follow-status")
  public ResponseEntity<Map<String, Boolean>> getFollowStatus(
      @PathVariable String name,
      @RequestAttribute Long userId) {

    boolean isFollowing = hashtagService.isHashtagFollowedByUser(name, userId);

    Map<String, Boolean> response = new HashMap<>();
    response.put("isFollowing", isFollowing);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/followed")
  public ResponseEntity<Page<HashtagResponseDTO>> getFollowedHashtags(
      @RequestAttribute Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<HashtagResponseDTO> hashtags = hashtagService.getFollowedHashtags(userId, pageable);
    return ResponseEntity.ok(hashtags);
  }

  @GetMapping("/suggestions")
  public ResponseEntity<List<HashtagResponseDTO>> getHashtagSuggestions(
      @RequestParam String query,
      @RequestParam(defaultValue = "5") int limit) {

    Pageable pageable = PageRequest.of(0, limit);
    Page<HashtagResponseDTO> suggestions = hashtagService.searchHashtags(query, pageable);
    return ResponseEntity.ok(suggestions.getContent());
  }
}
