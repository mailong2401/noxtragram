package com.noxtragram.model.dto.request;

import java.util.List;

public class ForwardMessageRequestDTO {
  private Long messageId;
  private List<Long> receiverIds;

  public Long getMessageId() {
    return messageId;
  }

  public void setMessageId(Long messageId) {
    this.messageId = messageId;
  }

  public List<Long> getReceiverIds() {
    return receiverIds;
  }

  public void setReceiverIds(List<Long> receiverIds) {
    this.receiverIds = receiverIds;
  }
}
