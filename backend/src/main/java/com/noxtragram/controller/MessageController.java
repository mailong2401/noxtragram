package com.noxtragram.controller;

import com.noxtragram.model.dto.request.*;
import com.noxtragram.model.dto.response.ApiResponse;
import com.noxtragram.model.dto.response.MessageResponseDTO;
import com.noxtragram.model.entity.Message;
import com.noxtragram.model.entity.MessageType;
import com.noxtragram.service.MessageService;
import com.noxtragram.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
public class MessageController {

  private final MessageService messageService;
  private final UserService userService;
  private final SimpMessagingTemplate messagingTemplate;

  public MessageController(MessageService messageService,
      UserService userService,
      SimpMessagingTemplate messagingTemplate) {
    this.messageService = messageService;
    this.userService = userService;
    this.messagingTemplate = messagingTemplate;
  }

  // ================= REST API =================

  @PostMapping("/send/text")
  public ResponseEntity<ApiResponse<MessageResponseDTO>> sendTextMessage(
      @RequestBody TextMessageRequestDTO request,
      Authentication authentication) {
    try {
      Long senderId = getUserIdFromAuthentication(authentication);
      Message message = messageService.sendTextMessage(senderId, request.getReceiverId(), request.getContent());

      MessageResponseDTO dto = convertToMessageDTO(message);

      // Gửi realtime tới receiver
      messagingTemplate.convertAndSendToUser(
          request.getReceiverId().toString(),
          "/queue/messages",
          dto);

      return ResponseEntity.ok(ApiResponse.success("Tin nhắn đã được gửi", dto));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
    }
  }

  @GetMapping("/history/{otherUserId}/page")
  public ResponseEntity<ApiResponse<Page<MessageResponseDTO>>> getMessageHistoryWithPagination(
      @PathVariable Long otherUserId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      Authentication authentication) {
    try {
      Long currentUserId = getUserIdFromAuthentication(authentication);
      Pageable pageable = PageRequest.of(page, size);
      Page<Message> messages = messageService.getMessageHistoryWithPagination(currentUserId, otherUserId, pageable);
      Page<MessageResponseDTO> dtos = messages.map(this::convertToMessageDTO);
      return ResponseEntity.ok(ApiResponse.success("Lấy lịch sử tin nhắn thành công", dtos));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
    }
  }

  @PutMapping("/mark-read/{senderId}")
  public ResponseEntity<ApiResponse<Void>> markMessagesAsRead(
      @PathVariable Long senderId,
      Authentication authentication) {
    try {
      Long receiverId = getUserIdFromAuthentication(authentication);
      messageService.markMessagesAsRead(receiverId, senderId);

      // Gửi realtime read receipt tới sender
      messagingTemplate.convertAndSendToUser(
          senderId.toString(),
          "/queue/read-receipt",
          Map.of("readerId", receiverId));

      return ResponseEntity.ok(ApiResponse.success("Đã đánh dấu tin nhắn là đã đọc", null));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
    }
  }

  // ================= WebSocket =================

  @MessageMapping("/chat.send")
  public void handleSendMessage(@Payload Map<String, Object> payload,
      SimpMessageHeaderAccessor headerAccessor) {
    try {
      Long senderId = (Long) headerAccessor.getSessionAttributes().get("userId");
      Long receiverId = Long.valueOf(payload.get("receiverId").toString());
      String content = (String) payload.get("content");
      String messageType = (String) payload.getOrDefault("messageType", "TEXT");
      String mediaUrl = (String) payload.get("mediaUrl");

      Message message = messageService.sendMessage(
          senderId,
          receiverId,
          content,
          MessageType.valueOf(messageType),
          mediaUrl);

      // Gửi realtime tới receiver
      messagingTemplate.convertAndSendToUser(
          receiverId.toString(),
          "/queue/messages",
          convertToMessageDTO(message));

    } catch (Exception e) {
      System.err.println("WebSocket send message error: " + e.getMessage());
    }
  }

  @MessageMapping("/chat.typing")
  public void handleTypingIndicator(@Payload Map<String, Object> typingData,
      SimpMessageHeaderAccessor headerAccessor) {
    try {
      Long senderId = (Long) headerAccessor.getSessionAttributes().get("userId");
      Long receiverId = Long.valueOf(typingData.get("receiverId").toString());
      Boolean isTyping = (Boolean) typingData.get("isTyping");

      messagingTemplate.convertAndSendToUser(
          receiverId.toString(),
          "/queue/typing",
          Map.of("senderId", senderId, "isTyping", isTyping));
    } catch (Exception e) {
      System.err.println("WebSocket typing error: " + e.getMessage());
    }
  }

  @MessageMapping("/chat.read-receipt")
  public void handleReadReceipt(@Payload Map<String, Object> readData,
      SimpMessageHeaderAccessor headerAccessor) {
    try {
      Long readerId = (Long) headerAccessor.getSessionAttributes().get("userId");
      Long senderId = Long.valueOf(readData.get("senderId").toString());

      messageService.markMessagesAsRead(readerId, senderId);

      messagingTemplate.convertAndSendToUser(
          senderId.toString(),
          "/queue/read-receipt",
          Map.of("readerId", readerId));
    } catch (Exception e) {
      System.err.println("WebSocket read receipt error: " + e.getMessage());
    }
  }

  // ================= Private Helpers =================

  private Long getUserIdFromAuthentication(Authentication authentication) {
    String username = authentication.getName();
    return userService.getUserIdByUsername(username); // Lấy đúng userId từ DB
  }

  private MessageResponseDTO convertToMessageDTO(Message message) {
    MessageResponseDTO dto = new MessageResponseDTO();
    dto.setId(message.getId());
    dto.setContent(message.getContent());
    dto.setImageUrl(message.getImageUrl());
    dto.setIsRead(message.getIsRead());
    dto.setMessageType(message.getMessageType().name());
    dto.setCreatedAt(message.getCreatedAt());
    dto.setSenderId(message.getSender().getId());
    dto.setSenderUsername(message.getSender().getUsername());
    dto.setSenderProfilePicture(message.getSender().getProfilePicture());
    dto.setReceiverId(message.getReceiver().getId());
    dto.setReceiverUsername(message.getReceiver().getUsername());
    return dto;
  }
}
