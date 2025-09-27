package com.noxtragram.model.dto.response;

import com.noxtragram.model.dto.Summary.PostSummaryDTO;

import java.time.LocalDateTime;
import java.util.List;

public class HashtagResponseDTO {
  private Long id;
  private String name;
  private Integer postCount;
  private LocalDateTime createdAt;
  private List<PostSummaryDTO> recentPosts;
  private Boolean isFollowing; // Cho tính năng follow hashtag

  // Constructors
  public HashtagResponseDTO() {
  }

  public HashtagResponseDTO(Long id, String name, Integer postCount, LocalDateTime createdAt) {
    this.id = id;
    this.name = name;
    this.postCount = postCount;
    this.createdAt = createdAt;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getPostCount() {
    return postCount;
  }

  public void setPostCount(Integer postCount) {
    this.postCount = postCount;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public List<PostSummaryDTO> getRecentPosts() {
    return recentPosts;
  }

  public void setRecentPosts(List<PostSummaryDTO> recentPosts) {
    this.recentPosts = recentPosts;
  }

  public Boolean getIsFollowing() {
    return isFollowing;
  }

  public void setIsFollowing(Boolean following) {
    isFollowing = following;
  }
}
