package com.noxtragram.model.dto.response;

import java.time.LocalDateTime;

public class LikeResponseDTO {
  private Long id;
  private Long userId;
  private String username;
  private String profilePicture;
  private Long postId;
  private LocalDateTime createdAt;

  // Constructors
  public LikeResponseDTO() {
  }

  public LikeResponseDTO(Long id, Long userId, String username, String profilePicture,
      Long postId, LocalDateTime createdAt) {
    this.id = id;
    this.userId = userId;
    this.username = username;
    this.profilePicture = profilePicture;
    this.postId = postId;
    this.createdAt = createdAt;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getProfilePicture() {
    return profilePicture;
  }

  public void setProfilePicture(String profilePicture) {
    this.profilePicture = profilePicture;
  }

  public Long getPostId() {
    return postId;
  }

  public void setPostId(Long postId) {
    this.postId = postId;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
