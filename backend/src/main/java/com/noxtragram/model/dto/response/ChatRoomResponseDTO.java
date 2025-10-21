package com.noxtragram.model.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatRoomResponseDTO {
  private Long id;
  private UserResponseDTO user1;
  private UserResponseDTO user2;
  private MessageResponseDTO lastMessage;
  private Long unreadCount;
  private LocalDateTime lastActivity;
  private LocalDateTime createdAt;
}
