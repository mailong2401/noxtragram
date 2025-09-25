package com.noxtragram.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_saves", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "post_id" }))
public class PostSave {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "folder_name")
  private String folderName = "All Posts"; // Thư mục lưu bài viết

  // ⏰ Timestamps
  @CreationTimestamp
  @Column(name = "saved_at", updatable = false)
  private LocalDateTime savedAt;

  // 🔗 Relationships

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  // 🏗️ Constructors
  public PostSave() {
  }

  public PostSave(User user, Post post) {
    this.user = user;
    this.post = post;
  }

  public PostSave(User user, Post post, String folderName) {
    this.user = user;
    this.post = post;
    this.folderName = folderName;
  }

  // 📊 Business Methods

  /**
   * Di chuyển bài viết sang thư mục khác
   */
  public void moveToFolder(String newFolderName) {
    this.folderName = newFolderName;
  }

  /**
   * Kiểm tra bài viết có trong thư mục cụ thể không
   */
  public boolean isInFolder(String folderName) {
    return this.folderName.equals(folderName);
  }

  // 🔄 Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Post post() {
    return post;
  }

  public void setPost(Post post) {
    this.post = post;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof PostSave postSave))
      return false;
    return getId() != null && getId().equals(postSave.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "PostSave{" +
        "id=" + id +
        ", folderName='" + folderName + '\'' +
        ", user=" + user.getUsername() +
        ", post=" + post.getId() +
        ", savedAt=" + savedAt +
        '}';
  }
}
