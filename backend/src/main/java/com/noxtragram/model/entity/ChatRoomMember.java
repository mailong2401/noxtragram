package com.noxtragram.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_members", uniqueConstraints = @UniqueConstraint(columnNames = { "chat_room_id", "user_id" }))
public class ChatRoomMember {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nickname")
  private String nickname; // Biệt danh trong nhóm

  @Column(name = "is_admin")
  private Boolean isAdmin = false;

  @Column(name = "is_muted")
  private Boolean isMuted = false; // Tắt thông báo

  @Column(name = "unread_count")
  private Integer unreadCount = 0; // Số tin chưa đọc của user này

  @Column(name = "last_read_at")
  private LocalDateTime lastReadAt; // Thời điểm đọc cuối cùng

  @Column(name = "joined_at")
  private LocalDateTime joinedAt;

  // ⏰ Timestamps
  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // 🔗 Relationships

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chat_room_id", nullable = false)
  private ChatRoom chatRoom;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  // 🏗️ Constructors
  public ChatRoomMember() {
  }

  public ChatRoomMember(ChatRoom chatRoom, User user) {
    this.chatRoom = chatRoom;
    this.user = user;
    this.joinedAt = LocalDateTime.now();
  }

  // 📊 Business Methods

  /**
   * Đánh dấu đã đọc tất cả tin nhắn
   */
  public void markAsRead() {
    this.unreadCount = 0;
    this.lastReadAt = LocalDateTime.now();
  }

  /**
   * Tăng số tin nhắn chưa đọc
   */
  public void incrementUnreadCount() {
    this.unreadCount++;
  }

  /**
   * Cấp quyền admin
   */
  public void promoteToAdmin() {
    this.isAdmin = true;
  }

  /**
   * Thu hồi quyền admin
   */
  public void demoteFromAdmin() {
    this.isAdmin = false;
  }

  /**
   * Tắt thông báo
   */
  public void mute() {
    this.isMuted = true;
  }

  /**
   * Bật thông báo
   */
  public void unmute() {
    this.isMuted = false;
  }

  /**
   * Kiểm tra có phải là người tạo phòng không
   */
  public boolean isRoomCreator() {
    return this.chatRoom.getCreatedBy() != null &&
        this.chatRoom.getCreatedBy().equals(this.user);
  }

  // 🔄 Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public Boolean getIsAdmin() {
    return isAdmin;
  }

  public void setIsAdmin(Boolean isAdmin) {
    this.isAdmin = isAdmin;
  }

  public Boolean getIsMuted() {
    return isMuted;
  }

  public void setIsMuted(Boolean isMuted) {
    this.isMuted = isMuted;
  }

  public Integer getUnreadCount() {
    return unreadCount;
  }

  public void setUnreadCount(Integer unreadCount) {
    this.unreadCount = unreadCount;
  }

  public LocalDateTime getLastReadAt() {
    return lastReadAt;
  }

  public void setLastReadAt(LocalDateTime lastReadAt) {
    this.lastReadAt = lastReadAt;
  }

  public LocalDateTime getJoinedAt() {
    return joinedAt;
  }

  public void setJoinedAt(LocalDateTime joinedAt) {
    this.joinedAt = joinedAt;
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

  public ChatRoom getChatRoom() {
    return chatRoom;
  }

  public void setChatRoom(ChatRoom chatRoom) {
    this.chatRoom = chatRoom;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
