package com.noxtragram.model.dto.response;

import java.time.LocalDateTime;

public class MessageResponseDTO {
  private Long id;
  private String content;
  private String imageUrl;
  private Boolean isRead;
  private String messageType;
  private LocalDateTime createdAt;

  // Thông tin người gửi
  private Long senderId;
  private String senderUsername;
  private String senderProfilePicture;

  // Thông tin người nhận
  private Long receiverId;
  private String receiverUsername;

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

  public String getMessageType() {
    return messageType;
  }

  public void setMessageType(String messageType) {
    this.messageType = messageType;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Long getSenderId() {
    return senderId;
  }

  public void setSenderId(Long senderId) {
    this.senderId = senderId;
  }

  public String getSenderUsername() {
    return senderUsername;
  }

  public void setSenderUsername(String senderUsername) {
    this.senderUsername = senderUsername;
  }

  public String getSenderProfilePicture() {
    return senderProfilePicture;
  }

  public void setSenderProfilePicture(String senderProfilePicture) {
    this.senderProfilePicture = senderProfilePicture;
  }

  public Long getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(Long receiverId) {
    this.receiverId = receiverId;
  }

  public String getReceiverUsername() {
    return receiverUsername;
  }

  public void setReceiverUsername(String receiverUsername) {
    this.receiverUsername = receiverUsername;
  }
}
