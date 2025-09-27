package com.noxtragram.model.dto.request;

import jakarta.validation.constraints.Size;
import java.util.List;

public class PostRequestDTO {

  @Size(max = 2200, message = "Caption must not exceed 2200 characters")
  private String caption;

  private String imageUrl;
  private List<String> imageUrls;
  private String videoUrl;
  private String location;
  private List<String> hashtags;

  // Constructors
  public PostRequestDTO() {
  }

  public PostRequestDTO(String caption, String imageUrl, List<String> imageUrls,
      String videoUrl, String location, List<String> hashtags) {
    this.caption = caption;
    this.imageUrl = imageUrl;
    this.imageUrls = imageUrls;
    this.videoUrl = videoUrl;
    this.location = location;
    this.hashtags = hashtags;
  }

  // Getters and Setters
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

  public List<String> getHashtags() {
    return hashtags;
  }

  public void setHashtags(List<String> hashtags) {
    this.hashtags = hashtags;
  }
}
