package com.noxtragram.model.dto.Summary;

import java.time.LocalDateTime;

public class PostSummaryDTO {
  private Long id;
  private String imageUrl;
  private String caption;
  private Integer likeCount;
  private Integer commentCount;
  private LocalDateTime createdAt;

  // Constructors
  public PostSummaryDTO() {
  }

  public PostSummaryDTO(Long id, String imageUrl, String caption, Integer likeCount,
      Integer commentCount, LocalDateTime createdAt) {
    this.id = id;
    this.imageUrl = imageUrl;
    this.caption = caption;
    this.likeCount = likeCount;
    this.commentCount = commentCount;
    this.createdAt = createdAt;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getCaption() {
    return caption;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  public Integer getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(Integer likeCount) {
    this.likeCount = likeCount;
  }

  public Integer getCommentCount() {
    return commentCount;
  }

  public void setCommentCount(Integer commentCount) {
    this.commentCount = commentCount;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
