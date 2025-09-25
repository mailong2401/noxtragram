package com.noxtragram.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hashtags", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Hashtag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", unique = true, nullable = false)
  private String name;

  @Column(name = "post_count")
  private Integer postCount = 0;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  // 🔗 Relationships

  @ManyToMany(mappedBy = "hashtags")
  private List<Post> posts = new ArrayList<>();

  // 🏗️ Constructors
  public Hashtag() {
  }

  public Hashtag(String name) {
    this.name = name;
  }

  // 📊 Business Methods

  /**
   * Tăng số lượng post sử dụng hashtag
   */
  public void incrementPostCount() {
    this.postCount++;
  }

  /**
   * Giảm số lượng post sử dụng hashtag
   */
  public void decrementPostCount() {
    if (this.postCount > 0) {
      this.postCount--;
    }
  }

  // 🔄 Getters and Setters
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

  public List<Post> getPosts() {
    return posts;
  }

  public void setPosts(List<Post> posts) {
    this.posts = posts;
  }
}
