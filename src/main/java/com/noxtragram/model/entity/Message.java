package com.noxtragram.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Message content is required")
  @Size(max = 2000, message = "Message must not exceed 2000 characters")
  @Column(nullable = false, length = 2000)
  private String content;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(name = "is_read")
  private Boolean isRead = false;

  @Column(name = "is_deleted_for_sender")
  private Boolean isDeletedForSender = false;

  @Column(name = "is_deleted_for_receiver")
  private Boolean isDeletedForReceiver = false;

  @Enumerated(EnumType.STRING)
  @Column(name = "message_type")
  private MessageType messageType = MessageType.TEXT;

  // ⏰ Timestamps
  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  // 🔗 Relationships

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id", nullable = false)
  private User sender;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "receiver_id", nullable = false)
  private User receiver;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chat_room_id")
  private ChatRoom chatRoom;

  // 🏗️ Constructors
  public Message() {
  }

  public Message(String content, User sender, User receiver) {
    this.content = content;
    this.sender = sender;
    this.receiver = receiver;
  }

  // 📊 Business Methods

  /**
   * Đánh dấu tin nhắn đã đọc
   */
  public void markAsRead() {
    this.isRead = true;
  }

  /**
   * Kiểm tra tin nhắn có thể xem được không
   */
  public boolean isVisibleForUser(User user) {
    if (user.equals(sender)) {
      return !isDeletedForSender;
    } else if (user.equals(receiver)) {
      return !isDeletedForReceiver;
    }
    return false;
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

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public Boolean getIsRead() {
    return isRead;
  }

  public void setIsRead(Boolean isRead) {
    this.isRead = isRead;
  }

  public Boolean getIsDeletedForSender() {
    return isDeletedForSender;
  }

  public void setIsDeletedForSender(Boolean isDeletedForSender) {
    this.isDeletedForSender = isDeletedForSender;
  }

  public Boolean getIsDeletedForReceiver() {
    return isDeletedForReceiver;
  }

  public void setIsDeletedForReceiver(Boolean isDeletedForReceiver) {
    this.isDeletedForReceiver = isDeletedForReceiver;
  }

  public MessageType getMessageType() {
    return messageType;
  }

  public void setMessageType(MessageType messageType) {
    this.messageType = messageType;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public User getSender() {
    return sender;
  }

  public void setSender(User sender) {
    this.sender = sender;
  }

  public User getReceiver() {
    return receiver;
  }

  public void setReceiver(User receiver) {
    this.receiver = receiver;
  }

  public ChatRoom getChatRoom() {
    return chatRoom;
  }

  public void setChatRoom(ChatRoom chatRoom) {
    this.chatRoom = chatRoom;
  }
}

enum MessageType {
  TEXT,
  IMAGE,
  VIDEO,
  VOICE
}
