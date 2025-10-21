package com.noxtragram.model.dto.request;

public class TextMessageRequestDTO {
  private Long receiverId;
  private String content;

  public Long getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(Long receiverId) {
    this.receiverId = receiverId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
