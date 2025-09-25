package com.noxtragram.model.dto;

public class TrendingHashtagDTO {
  private String name;
  private Integer postCount;
  private Integer trendScore; // Điểm trending dựa trên tương tác

  // Constructors
  public TrendingHashtagDTO() {
  }

  public TrendingHashtagDTO(String name, Integer postCount, Integer trendScore) {
    this.name = name;
    this.postCount = postCount;
    this.trendScore = trendScore;
  }

  // Getters and Setters
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

  public Integer getTrendScore() {
    return trendScore;
  }

  public void setTrendScore(Integer trendScore) {
    this.trendScore = trendScore;
  }
}
