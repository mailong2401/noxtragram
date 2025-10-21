package com.noxtragram.model.dto.request;

public class StickerMessageRequestDTO {
  private Long receiverId;
  private String stickerId;

  public Long getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(Long receiverId) {
    this.receiverId = receiverId;
  }

  public String getStickerId() {
    return stickerId;
  }

  public void setStickerId(String stickerId) {
    this.stickerId = stickerId;
  }
}
