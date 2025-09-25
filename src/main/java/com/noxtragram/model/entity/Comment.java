package com.noxtragram.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Comment content is required")
  @Size(max = 1000, message = "Comment must not exceed 1000 characters")
  @Column(nullable = false, length = 1000)
  private String content;

  @Column(name = "like_count")
  private Integer likeCount = 0;

  @Column(name = "is_deleted")
  private Boolean isDeleted = false;

  // ⏰ Timestamps
  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // 🔗 Relationships

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_comment_id")
  private Comment parentComment; // Cho comment reply

  @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> replies = new ArrayList<>();

  @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CommentLike> likes = new ArrayList<>();

  // 🏗️ Constructors
  public Comment() {
  }

  public Comment(String content, User user, Post post) {
    this.content = content;
    this.user = user;
    this.post = post;
  }

  // 📊 Business Methods

  /**
   * Kiểm tra comment có phải là reply không
   */
  public boolean isReply() {
    return this.parentComment != null;
  }

  /**
   * Tăng số lượng like
   */
  public void incrementLikeCount() {
    this.likeCount++;
  }

  /**
   * Thêm reply vào comment
   */
  public void addReply(Comment reply) {
    this.replies.add(reply);
    reply.setParentComment(this);
  }

  // 🔄 Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Integer getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(Integer likeCount) {
    this.likeCount = likeCount;
  }

  public Boolean getIsDeleted() {
    return isDeleted;
  }

  public void setIsDeleted(Boolean isDeleted) {
    this.isDeleted = isDeleted;
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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Post getPost() {
    return post;
  }

  public void setPost(Post post) {
    this.post = post;
  }

  public Comment getParentComment() {
    return parentComment;
  }

  public void setParentComment(Comment parentComment) {
    this.parentComment = parentComment;
  }

  public List<Comment> getReplies() {
    return replies;
  }

  public void setReplies(List<Comment> replies) {
    this.replies = replies;
  }

  public List<CommentLike> getLikes() {
    return likes;
  }

  public void setLikes(List<CommentLike> likes) {
    this.likes = likes;
  }
}
