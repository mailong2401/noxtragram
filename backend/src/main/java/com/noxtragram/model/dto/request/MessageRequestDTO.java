package com.noxtragram.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequestDTO {

  @NotBlank(message = "Nội dung tin nhắn không được để trống")
  private String content;

  private String imageUrl;

  @NotNull(message = "Người nhận là bắt buộc")
  private Long receiverId;

  private Long chatRoomId;

  private String messageType = "TEXT";
}
