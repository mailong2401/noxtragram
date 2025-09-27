package com.noxtragram.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name; // Tên phòng (cho group chat)

  @Column(name = "description")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "chat_type", nullable = false)
  private ChatType chatType = ChatType.DIRECT;

  @Column(name = "is_active")
  private Boolean isActive = true;

  @Column(name = "last_message")
  private String lastMessage; // Tin nhắn cuối cùng

  @Column(name = "last_message_at")
  private LocalDateTime lastMessageAt;

  @Column(name = "unread_count")
  private Integer unreadCount = 0; // Tổng số tin chưa đọc

  // ⏰ Timestamps
  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // 🔗 Relationships

  // 👥 Thành viên trong phòng chat
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "chat_room_members", joinColumns = @JoinColumn(name = "chat_room_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
  private Set<User> members = new HashSet<>();

  // 💌 Tin nhắn trong phòng
  @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("createdAt ASC")
  private List<Message> messages = new ArrayList<>();

  // 👑 Người tạo phòng (cho group chat)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by")
  private User createdBy;

  // 🔧 Cài đặt của từng user trong phòng
  @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ChatRoomMember> memberSettings = new ArrayList<>();

  // 🏗️ Constructors
  public ChatRoom() {
  }

  // Constructor cho direct message
  public ChatRoom(ChatType chatType) {
    this.chatType = chatType;
  }

  // Constructor cho group chat
  public ChatRoom(String name, String description, User createdBy) {
    this.name = name;
    this.description = description;
    this.createdBy = createdBy;
    this.chatType = ChatType.GROUP;
    this.members.add(createdBy);
  }

  // 📊 Business Methods

  /**
   * Thêm thành viên vào phòng chat
   */
  public void addMember(User user) {
    if (!this.members.contains(user)) {
      this.members.add(user);
      // Tạo settings mới cho thành viên
      ChatRoomMember memberSettings = new ChatRoomMember(this, user);
      this.memberSettings.add(memberSettings);
    }
  }

  /**
   * Xóa thành viên khỏi phòng chat
   */
  public void removeMember(User user) {
    this.members.remove(user);
    this.memberSettings.removeIf(ms -> ms.getUser().equals(user));
  }

  /**
   * Kiểm tra user có trong phòng chat không
   */
  public boolean hasMember(User user) {
    return this.members.contains(user);
  }

  /**
   * Lấy số lượng thành viên
   */
  public int getMemberCount() {
    return this.members.size();
  }

  /**
   * Cập nhật tin nhắn cuối cùng
   */
  public void updateLastMessage(String message, LocalDateTime timestamp) {
    this.lastMessage = message;
    this.lastMessageAt = timestamp;
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Tăng số tin nhắn chưa đọc
   */
  public void incrementUnreadCount() {
    this.unreadCount++;
  }

  /**
   * Reset số tin nhắn chưa đọc
   */
  public void resetUnreadCount() {
    this.unreadCount = 0;
  }

  /**
   * Kiểm tra có phải direct message không
   */
  public boolean isDirectMessage() {
    return this.chatType == ChatType.DIRECT;
  }

  /**
   * Kiểm tra có phải group chat không
   */
  public boolean isGroupChat() {
    return this.chatType == ChatType.GROUP;
  }

  /**
   * Lấy tên hiển thị của phòng chat
   */
  public String getDisplayName(User currentUser) {
    if (this.isDirectMessage()) {
      // Cho direct message, hiển thị tên của user kia
      return this.members.stream()
          .filter(member -> !member.equals(currentUser))
          .findFirst()
          .map(User::getUsername)
          .orElse("Unknown User");
    } else {
      // Cho group chat, hiển thị tên phòng hoặc tên các thành viên
      return this.name != null ? this.name
          : this.members.stream()
              .map(User::getUsername)
              .limit(3)
              .reduce((a, b) -> a + ", " + b)
              .orElse("Group Chat");
    }
  }

  /**
   * Lấy ảnh đại diện phòng chat
   */
  public String getDisplayImage(User currentUser) {
    if (this.isDirectMessage()) {
      // Cho direct message, lấy avatar của user kia
      return this.members.stream()
          .filter(member -> !member.equals(currentUser))
          .findFirst()
          .map(User::getProfilePicture)
          .orElse(null);
    } else {
      // Cho group chat, có thể dùng ảnh mặc định hoặc ảnh đầu tiên của thành viên
      return this.members.stream()
          .findFirst()
          .map(User::getProfilePicture)
          .orElse(null);
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ChatType getChatType() {
    return chatType;
  }

  public void setChatType(ChatType chatType) {
    this.chatType = chatType;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public String getLastMessage() {
    return lastMessage;
  }

  public void setLastMessage(String lastMessage) {
    this.lastMessage = lastMessage;
  }

  public LocalDateTime getLastMessageAt() {
    return lastMessageAt;
  }

  public void setLastMessageAt(LocalDateTime lastMessageAt) {
    this.lastMessageAt = lastMessageAt;
  }

  public Integer getUnreadCount() {
    return unreadCount;
  }

  public void setUnreadCount(Integer unreadCount) {
    this.unreadCount = unreadCount;
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

  public Set<User> getMembers() {
    return members;
  }

  public void setMembers(Set<User> members) {
    this.members = members;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public List<ChatRoomMember> getMemberSettings() {
    return memberSettings;
  }

  public void setMemberSettings(List<ChatRoomMember> memberSettings) {
    this.memberSettings = memberSettings;
  }
}

enum ChatType {
  DIRECT, // Chat 1-1
  GROUP // Chat nhóm
}
