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

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id", nullable = false)
  private User sender;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "receiver_id", nullable = false)
  private User receiver;

  public Message() {
  }

  public Message(String content, User sender, User receiver) {
    this.content = content;
    this.sender = sender;
    this.receiver = receiver;
  }

  // Thêm constructor cho các loại tin nhắn khác
  public Message(String content, User sender, User receiver, MessageType messageType) {
    this.content = content;
    this.sender = sender;
    this.receiver = receiver;
    this.messageType = messageType;
  }

  public Message(String content, String imageUrl, User sender, User receiver, MessageType messageType) {
    this.content = content;
    this.imageUrl = imageUrl;
    this.sender = sender;
    this.receiver = receiver;
    this.messageType = messageType;
  }

  // Business Methods
  public void markAsRead() {
    this.isRead = true;
  }

  public boolean isVisibleForUser(User user) {
    if (user.equals(sender)) {
      return !isDeletedForSender;
    } else if (user.equals(receiver)) {
      return !isDeletedForReceiver;
    }
    return false;
  }

  // Helper methods for message type
  public boolean isMediaMessage() {
    return this.messageType.isMedia();
  }

  public boolean isFileMessage() {
    return this.messageType.isFile();
  }

  public boolean isSystemMessage() {
    return this.messageType.isSystem();
  }

  public String getMessageIcon() {
    return this.messageType.getIcon();
  }

  public boolean isDeletable() {
    return !this.messageType.isSystem();
  }

  public String getPreview() {
    switch (this.messageType) {
      case TEXT:
        return this.content;
      case IMAGE:
        return "🖼️ Hình ảnh";
      case VIDEO:
        return "🎥 Video";
      case VOICE:
        return "🎤 Tin nhắn thoại";
      case FILE:
        return "📎 Tệp đính kèm";
      case LOCATION:
        return "📍 Vị trí";
      case STICKER:
        return "😊 Nhãn dán";
      case SYSTEM:
        return "⚙️ " + this.content;
      default:
        return this.content;
    }
  }

  // Getters and Setters
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

}
