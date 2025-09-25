package com.noxtragram.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment_likes", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "comment_id" }))
public class CommentLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // ⏰ Timestamps
  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  // 🔗 Relationships

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comment_id", nullable = false)
  private Comment comment;

  // 🏗️ Constructors
  public CommentLike() {
  }

  public CommentLike(User user, Comment comment) {
    this.user = user;
    this.comment = comment;
  }

  // 📊 Business Methods

  /**
   * Kiểm tra user có thể like comment không
   */
  public boolean canLike(User user) {
    return !this.user.equals(user) &&
        !this.comment.getUser().equals(user); // Không tự like comment của mình
  }

  // 🔄 Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Comment getComment() {
    return comment;
  }

  public void setComment(Comment comment) {
    this.comment = comment;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof CommentLike that))
      return false;
    return getId() != null && getId().equals(that.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "CommentLike{" +
        "id=" + id +
        ", user=" + user.getUsername() +
        ", comment=" + comment.getId() +
        ", createdAt=" + createdAt +
        '}';
  }
}
