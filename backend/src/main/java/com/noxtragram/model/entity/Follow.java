package com.noxtragram.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "follows", uniqueConstraints = @UniqueConstraint(columnNames = { "follower_id", "following_id" }))
public class Follow {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 🔗 Relationships

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "follower_id", nullable = false)
  private User follower; // Người theo dõi

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "following_id", nullable = false)
  private User following; // Người được theo dõi

  // ⏰ Timestamps
  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  // 📊 Additional fields
  @Column(name = "is_notifications_enabled")
  private Boolean isNotificationsEnabled = true; // Bật thông báo mặc định

  // 🏗️ Constructors
  public Follow() {
  }

  public Follow(User follower, User following) {
    this.follower = follower;
    this.following = following;
  }

  public Follow(User follower, User following, Boolean isNotificationsEnabled) {
    this.follower = follower;
    this.following = following;
    this.isNotificationsEnabled = isNotificationsEnabled;
  }

  // 📊 Business Methods

  /**
   * Bật/tắt thông báo cho follow
   */
  public void toggleNotifications() {
    this.isNotificationsEnabled = !this.isNotificationsEnabled;
  }

  /**
   * Kiểm tra xem follow có hợp lệ không (không tự follow chính mình)
   */
  public boolean isValid() {
    return !follower.getId().equals(following.getId());
  }

  // 🔄 Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getFollower() {
    return follower;
  }

  public void setFollower(User follower) {
    this.follower = follower;
  }

  public User getFollowing() {
    return following;
  }

  public void setFollowing(User following) {
    this.following = following;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Boolean getIsNotificationsEnabled() {
    return isNotificationsEnabled;
  }

  public void setIsNotificationsEnabled(Boolean isNotificationsEnabled) {
    this.isNotificationsEnabled = isNotificationsEnabled;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Follow follow))
      return false;
    return getId() != null && getId().equals(follow.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "Follow{" +
        "id=" + id +
        ", follower=" + (follower != null ? follower.getUsername() : "null") +
        ", following=" + (following != null ? following.getUsername() : "null") +
        ", createdAt=" + createdAt +
        ", isNotificationsEnabled=" + isNotificationsEnabled +
        '}';
  }
}
