package com.noxtragram.model.dto.response;

import com.noxtragram.model.dto.Summary.UserSummaryDTO;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDTO {
  private Long id;
  private String caption;
  private String imageUrl;
  private List<String> imageUrls;
  private String videoUrl;
  private String location;
  private Integer likeCount;
  private Integer commentCount;
  private Integer shareCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private UserSummaryDTO user;
  private List<String> hashtags;
  private Boolean isLikedByCurrentUser;
  private Boolean isSavedByCurrentUser;

  // Constructors
  public PostResponseDTO() {
  }

  public PostResponseDTO(Long id, String caption, String imageUrl, List<String> imageUrls,
      String videoUrl, String location, Integer likeCount, Integer commentCount,
      Integer shareCount, LocalDateTime createdAt, LocalDateTime updatedAt,
      UserSummaryDTO user, List<String> hashtags) {
    this.id = id;
    this.caption = caption;
    this.imageUrl = imageUrl;
    this.imageUrls = imageUrls;
    this.videoUrl = videoUrl;
    this.location = location;
    this.likeCount = likeCount;
    this.commentCount = commentCount;
    this.shareCount = shareCount;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.user = user;
    this.hashtags = hashtags;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCaption() {
    return caption;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public List<String> getImageUrls() {
    return imageUrls;
  }

  public void setImageUrls(List<String> imageUrls) {
    this.imageUrls = imageUrls;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public void setVideoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
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

  public Integer getShareCount() {
    return shareCount;
  }

  public void setShareCount(Integer shareCount) {
    this.shareCount = shareCount;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public UserSummaryDTO getUser() {
    return user;
  }

  public void setUser(UserSummaryDTO user) {
    this.user = user;
  }

  public List<String> getHashtags() {
    return hashtags;
  }

  public void setHashtags(List<String> hashtags) {
    this.hashtags = hashtags;
  }

  public Boolean getIsLikedByCurrentUser() {
    return isLikedByCurrentUser;
  }

  public void setIsLikedByCurrentUser(Boolean likedByCurrentUser) {
    isLikedByCurrentUser = likedByCurrentUser;
  }

  public Boolean getIsSavedByCurrentUser() {
    return isSavedByCurrentUser;
  }

  public void setIsSavedByCurrentUser(Boolean savedByCurrentUser) {
    isSavedByCurrentUser = savedByCurrentUser;
  }
}
