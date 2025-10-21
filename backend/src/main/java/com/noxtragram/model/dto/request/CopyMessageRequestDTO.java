package com.noxtragram.model.dto.request;

public class CopyMessageRequestDTO {
  private Long messageId;
  private Long receiverId;

  public Long getMessageId() {
    return messageId;
  }

  public void setMessageId(Long messageId) {
    this.messageId = messageId;
  }

  public Long getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(Long receiverId) {
    this.receiverId = receiverId;
  }
}
