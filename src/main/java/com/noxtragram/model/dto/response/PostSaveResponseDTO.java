package com.noxtragram.model.dto.response;

import java.time.LocalDateTime;

public class PostSaveResponseDTO {
  private Long id;
  private Long userId;
  private String username;
  private Long postId;
  private String folderName;
  private LocalDateTime savedAt;
  private PostResponseDTO postDetails;

  // Constructors
  public PostSaveResponseDTO() {
  }

  public PostSaveResponseDTO(Long id, Long userId, String username, Long postId,
      String folderName, LocalDateTime savedAt) {
    this.id = id;
    this.userId = userId;
    this.username = username;
    this.postId = postId;
    this.folderName = folderName;
    this.savedAt = savedAt;
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

  public Long getPostId() {
    return postId;
  }

  public void setPostId(Long postId) {
    this.postId = postId;
  }

  public String getFolderName() {
    return folderName;
  }

  public void setFolderName(String folderName) {
    this.folderName = folderName;
  }

  public LocalDateTime getSavedAt() {
    return savedAt;
  }

  public void setSavedAt(LocalDateTime savedAt) {
    this.savedAt = savedAt;
  }

  public PostResponseDTO getPostDetails() {
    return postDetails;
  }

  public void setPostDetails(PostResponseDTO postDetails) {
    this.postDetails = postDetails;
  }
}
