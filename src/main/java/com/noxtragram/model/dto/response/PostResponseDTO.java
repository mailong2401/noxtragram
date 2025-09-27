package com.noxtragram.model.dto.response;

import com.noxtragram.model.dto.Summary.UserSummaryDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponseDTO {
  private Long id;
  private String caption;
  private String imageUrl;
  private List<String> imageUrls;
  private String videoUrl;
  private String location;
  private Integer likeCount = 0;
  private Integer commentCount = 0;
  private Integer shareCount = 0;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private UserSummaryDTO user;
  private List<String> hashtags;

  @JsonProperty("isLikedByCurrentUser")
  private Boolean isLikedByCurrentUser = false;

  @JsonProperty("isSavedByCurrentUser")
  private Boolean isSavedByCurrentUser = false;

  // Constructors
  public PostResponseDTO() {
    this.imageUrls = new ArrayList<>();
    this.hashtags = new ArrayList<>();
  }

  public PostResponseDTO(Long id, String caption, String imageUrl, List<String> imageUrls,
      String videoUrl, String location, Integer likeCount, Integer commentCount,
      Integer shareCount, LocalDateTime createdAt, LocalDateTime updatedAt,
      UserSummaryDTO user, List<String> hashtags) {
    this();
    this.id = id;
    this.caption = caption;
    this.imageUrl = imageUrl;
    setImageUrls(imageUrls); // Use setter to handle null
    this.videoUrl = videoUrl;
    this.location = location;
    this.likeCount = likeCount != null ? likeCount : 0;
    this.commentCount = commentCount != null ? commentCount : 0;
    this.shareCount = shareCount != null ? shareCount : 0;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.user = user;
    setHashtags(hashtags); // Use setter to handle null
  }

  // Builder pattern for easier object creation
  public static class Builder {
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

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder caption(String caption) {
      this.caption = caption;
      return this;
    }

    public Builder imageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
      return this;
    }

    public Builder imageUrls(List<String> imageUrls) {
      this.imageUrls = imageUrls;
      return this;
    }

    public Builder videoUrl(String videoUrl) {
      this.videoUrl = videoUrl;
      return this;
    }

    public Builder location(String location) {
      this.location = location;
      return this;
    }

    public Builder likeCount(Integer likeCount) {
      this.likeCount = likeCount;
      return this;
    }

    public Builder commentCount(Integer commentCount) {
      this.commentCount = commentCount;
      return this;
    }

    public Builder shareCount(Integer shareCount) {
      this.shareCount = shareCount;
      return this;
    }

    public Builder createdAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder updatedAt(LocalDateTime updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public Builder user(UserSummaryDTO user) {
      this.user = user;
      return this;
    }

    public Builder hashtags(List<String> hashtags) {
      this.hashtags = hashtags;
      return this;
    }

    public PostResponseDTO build() {
      return new PostResponseDTO(id, caption, imageUrl, imageUrls, videoUrl, location,
          likeCount, commentCount, shareCount, createdAt, updatedAt, user, hashtags);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  // Helper methods
  public boolean hasImages() {
    return (imageUrl != null && !imageUrl.isEmpty()) ||
        (imageUrls != null && !imageUrls.isEmpty());
  }

  public boolean hasVideo() {
    return videoUrl != null && !videoUrl.isEmpty();
  }

  public boolean hasLocation() {
    return location != null && !location.isEmpty();
  }

  public boolean hasHashtags() {
    return hashtags != null && !hashtags.isEmpty();
  }

  public String getFirstImage() {
    if (imageUrl != null && !imageUrl.isEmpty()) {
      return imageUrl;
    }
    if (imageUrls != null && !imageUrls.isEmpty()) {
      return imageUrls.get(0);
    }
    return null;
  }

  public List<String> getAllImages() {
    List<String> allImages = new ArrayList<>();
    if (imageUrl != null && !imageUrl.isEmpty()) {
      allImages.add(imageUrl);
    }
    if (imageUrls != null) {
      allImages.addAll(imageUrls);
    }
    return allImages;
  }

  // Getters and Setters với xử lý null
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
    if (imageUrls == null) {
      imageUrls = new ArrayList<>();
    }
    return imageUrls;
  }

  public void setImageUrls(List<String> imageUrls) {
    this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
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
    return likeCount != null ? likeCount : 0;
  }

  public void setLikeCount(Integer likeCount) {
    this.likeCount = likeCount != null ? likeCount : 0;
  }

  public Integer getCommentCount() {
    return commentCount != null ? commentCount : 0;
  }

  public void setCommentCount(Integer commentCount) {
    this.commentCount = commentCount != null ? commentCount : 0;
  }

  public Integer getShareCount() {
    return shareCount != null ? shareCount : 0;
  }

  public void setShareCount(Integer shareCount) {
    this.shareCount = shareCount != null ? shareCount : 0;
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
    if (hashtags == null) {
      hashtags = new ArrayList<>();
    }
    return hashtags;
  }

  public void setHashtags(List<String> hashtags) {
    this.hashtags = hashtags != null ? hashtags : new ArrayList<>();
  }

  public Boolean getIsLikedByCurrentUser() {
    return isLikedByCurrentUser != null ? isLikedByCurrentUser : false;
  }

  public void setIsLikedByCurrentUser(Boolean likedByCurrentUser) {
    this.isLikedByCurrentUser = likedByCurrentUser != null ? likedByCurrentUser : false;
  }

  public Boolean getIsSavedByCurrentUser() {
    return isSavedByCurrentUser != null ? isSavedByCurrentUser : false;
  }

  public void setIsSavedByCurrentUser(Boolean savedByCurrentUser) {
    this.isSavedByCurrentUser = savedByCurrentUser != null ? savedByCurrentUser : false;
  }

  @Override
  public String toString() {
    return "PostResponseDTO{" +
        "id=" + id +
        ", caption='" + caption + '\'' +
        ", likeCount=" + likeCount +
        ", commentCount=" + commentCount +
        ", user=" + (user != null ? user.getUsername() : "null") +
        '}';
  }
}
