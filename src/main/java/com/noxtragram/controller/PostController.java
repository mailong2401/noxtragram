package com.noxtragram.controller;

import com.noxtragram.model.dto.request.PostRequestDTO;
import com.noxtragram.model.dto.response.PostResponseDTO;
import com.noxtragram.model.entity.User;
import com.noxtragram.repository.UserRepository;
import com.noxtragram.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "http://localhost:3000") // ReactJS port
public class PostController {

  private final PostService postService;

  @Autowired
  private UserRepository userRepository;

  public PostController(PostService postService) {
    this.postService = postService;
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> createPost(
      @RequestPart(value = "caption", required = false) String caption,
      @RequestPart(value = "location", required = false) String location,
      @RequestPart(value = "hashtags", required = false) String hashtags,
      @RequestPart(value = "files", required = false) List<MultipartFile> files,
      Authentication authentication) {

    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      String username = authentication.getName();
      User user = userRepository.findByUsername(username)
          .orElseThrow(() -> new EntityNotFoundException("User not found"));

      // Validate files
      if (files != null) {
        for (MultipartFile file : files) {
          if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "File cannot be empty"));
          }

          // Validate file type
          String contentType = file.getContentType();
          if (contentType == null ||
              (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Only image and video files are allowed"));
          }
        }
      }

      PostRequestDTO postRequest = new PostRequestDTO();
      postRequest.setCaption(caption);
      postRequest.setLocation(location);

      if (hashtags != null && !hashtags.trim().isEmpty()) {
        // Sửa lỗi xử lý hashtags
        List<String> hashtagList = Arrays.stream(hashtags.split("[\\s,]+"))
            .filter(tag -> tag.startsWith("#"))
            .map(tag -> tag.replaceAll("[^a-zA-Z0-9#_]", "")) // Clean hashtag
            .filter(tag -> tag.length() > 1) // Ít nhất # + 1 ký tự
            .distinct()
            .collect(Collectors.toList());
        postRequest.setHashtags(hashtagList);
      }

      PostResponseDTO createdPost = postService.createPost(postRequest, files, user.getId());
      return ResponseEntity.ok(createdPost);
    } catch (Exception e) {
      Map<String, String> error = new HashMap<>();
      error.put("error", "Failed to create post: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
  }

  @GetMapping("/{postId}")
  public ResponseEntity<?> getPost(@PathVariable Long postId, Authentication authentication) {
    try {
      Long userId = getUserIdFromAuthentication(authentication);
      PostResponseDTO post = postService.getPostById(postId, userId);
      return ResponseEntity.ok(post);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Failed to get post"));
    }
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<?> getUserPosts(
      @PathVariable Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      Authentication authentication) {

    try {
      Long currentUserId = getUserIdFromAuthentication(authentication);
      Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
      Page<PostResponseDTO> posts = postService.getPostsByUserId(userId, currentUserId, pageable);
      return ResponseEntity.ok(posts);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Failed to get user posts"));
    }
  }

  @GetMapping("/feed")
  public ResponseEntity<?> getFeed(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      Authentication authentication) {

    try {
      Long userId = getUserIdFromAuthentication(authentication);
      Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
      Page<PostResponseDTO> posts = postService.getFeedPosts(userId, pageable);
      return ResponseEntity.ok(posts);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Failed to get feed"));
    }
  }

  @GetMapping("/hashtag/{hashtag}")
  public ResponseEntity<?> getPostsByHashtag(
      @PathVariable String hashtag,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      Authentication authentication) {

    try {
      Long userId = getUserIdFromAuthentication(authentication);
      Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
      Page<PostResponseDTO> posts = postService.getPostsByHashtag(hashtag, userId, pageable);
      return ResponseEntity.ok(posts);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Failed to get posts by hashtag"));
    }
  }

  @GetMapping("/saved")
  public ResponseEntity<?> getSavedPosts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      Authentication authentication) {

    try {
      Long userId = getUserIdFromAuthentication(authentication);
      Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
      Page<PostResponseDTO> posts = postService.getSavedPosts(userId, userId, pageable);
      return ResponseEntity.ok(posts);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Failed to get saved posts"));
    }
  }

  @GetMapping("/popular")
  public ResponseEntity<?> getPopularPosts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      Authentication authentication) {

    try {
      Long userId = getUserIdFromAuthentication(authentication);
      Pageable pageable = PageRequest.of(page, size, Sort.by("likeCount").descending());
      Page<PostResponseDTO> posts = postService.getPopularPosts(userId, pageable);
      return ResponseEntity.ok(posts);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Failed to get popular posts"));
    }
  }

  @PutMapping("/{postId}")
  public ResponseEntity<?> updatePost(
      @PathVariable Long postId,
      @Valid @RequestBody PostRequestDTO postRequest,
      Authentication authentication) {

    try {
      Long userId = getUserIdFromAuthentication(authentication);
      PostResponseDTO updatedPost = postService.updatePost(postId, postRequest, userId);
      return ResponseEntity.ok(updatedPost);
    } catch (SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Failed to update post"));
    }
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<?> deletePost(@PathVariable Long postId, Authentication authentication) {
    try {
      Long userId = getUserIdFromAuthentication(authentication);
      postService.deletePost(postId, userId);
      return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
    } catch (SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Failed to delete post"));
    }
  }

  @PostMapping("/{postId}/like")
  public ResponseEntity<?> likePost(@PathVariable Long postId, Authentication authentication) {
    try {
      Long userId = getUserIdFromAuthentication(authentication);
      postService.likePost(postId, userId);
      return ResponseEntity.ok(Map.of("message", "Post liked successfully"));
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Failed to like post"));
    }
  }

  @DeleteMapping("/{postId}/like")
  public ResponseEntity<?> unlikePost(@PathVariable Long postId, Authentication authentication) {
    try {
      Long userId = getUserIdFromAuthentication(authentication);
      postService.unlikePost(postId, userId);
      return ResponseEntity.ok(Map.of("message", "Post unliked successfully"));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Failed to unlike post"));
    }
  }

  @PostMapping("/{postId}/save")
  public ResponseEntity<?> savePost(@PathVariable Long postId, Authentication authentication) {
    try {
      Long userId = getUserIdFromAuthentication(authentication);
      postService.savePost(postId, userId);
      return ResponseEntity.ok(Map.of("message", "Post saved successfully"));
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Failed to save post"));
    }
  }

  @DeleteMapping("/{postId}/save")
  public ResponseEntity<?> unsavePost(@PathVariable Long postId, Authentication authentication) {
    try {
      Long userId = getUserIdFromAuthentication(authentication);
      postService.unsavePost(postId, userId);
      return ResponseEntity.ok(Map.of("message", "Post unsaved successfully"));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Failed to unsave post"));
    }
  }

  @GetMapping("/{postId}/interactions")
  public ResponseEntity<?> getPostInteractions(@PathVariable Long postId, Authentication authentication) {
    try {
      Long userId = getUserIdFromAuthentication(authentication);
      boolean isLiked = postService.isPostLikedByUser(postId, userId);
      boolean isSaved = postService.isPostSavedByUser(postId, userId);

      Map<String, Boolean> interactions = new HashMap<>();
      interactions.put("isLiked", isLiked);
      interactions.put("isSaved", isSaved);

      return ResponseEntity.ok(interactions);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Failed to get interactions"));
    }
  }

  private Long getUserIdFromAuthentication(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new SecurityException("User not authenticated");
    }

    String username = authentication.getName();
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    return user.getId();
  }
}
