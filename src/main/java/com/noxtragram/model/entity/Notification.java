package com.noxtragram.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title")
  private String title;

  @Column(name = "message", length = 500)
  private String message;

  @Column(name = "is_read")
  private Boolean isRead = false;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private NotificationType type;

  @Column(name = "related_id") // ID của post, user, etc.
  private Long relatedId;

  // ⏰ Timestamps
  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  // 🔗 Relationships

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_user_id")
  private User fromUser; // Người gây ra thông báo

  // 🏗️ Constructors
  public Notification() {
  }

  public Notification(NotificationType type, User user, User fromUser) {
    this.type = type;
    this.user = user;
    this.fromUser = fromUser;
  }

  // 📊 Business Methods

  /**
   * Đánh dấu thông báo đã đọc
   */
  public void markAsRead() {
    this.isRead = true;
  }

  /**
   * Tạo message tự động dựa trên type
   */
  public String generateMessage() {
    String fromUsername = fromUser != null ? fromUser.getUsername() : "Someone";

    return switch (type) {
      case LIKE -> fromUsername + " liked your post";
      case COMMENT -> fromUsername + " commented on your post";
      case FOLLOW -> fromUsername + " started following you";
      case MENTION -> fromUsername + " mentioned you in a comment";
      case MESSAGE -> "New message from " + fromUsername;
      default -> "You have a new notification";
    };
  }

  // 🔄 Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Boolean getIsRead() {
    return isRead;
  }

  public void setIsRead(Boolean isRead) {
    this.isRead = isRead;
  }

  public NotificationType getType() {
    return type;
  }

  public void setType(NotificationType type) {
    this.type = type;
  }

  public Long getRelatedId() {
    return relatedId;
  }

  public void setRelatedId(Long relatedId) {
    this.relatedId = relatedId;
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

  public User getFromUser() {
    return fromUser;
  }

  public void setFromUser(User fromUser) {
    this.fromUser = fromUser;
  }
}

enum NotificationType {
  LIKE,
  COMMENT,
  FOLLOW,
  MENTION,
  MESSAGE,
  SYSTEM
}
