package com.noxtragram.service.implementation;

import com.noxtragram.model.entity.Message;
import com.noxtragram.model.entity.MessageType;
import com.noxtragram.model.entity.User;
import com.noxtragram.repository.MessageRepository;
import com.noxtragram.repository.UserRepository;
import com.noxtragram.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final SimpMessagingTemplate messagingTemplate;

  // ============ SEND MESSAGE METHODS ============

  @Override
  @Transactional
  public Message sendTextMessage(Long senderId, Long receiverId, String content) {
    User sender = getUserById(senderId);
    User receiver = getUserById(receiverId);

    Message message = new Message(content, sender, receiver);
    message.setMessageType(MessageType.TEXT);

    Message savedMessage = messageRepository.save(message);
    sendWebSocketNotification(savedMessage, receiverId);

    log.info("Text message sent from {} to {}", senderId, receiverId);
    return savedMessage;
  }

  @Override
  @Transactional
  public Message sendImageMessage(Long senderId, Long receiverId, String imageUrl, String caption) {
    User sender = getUserById(senderId);
    User receiver = getUserById(receiverId);

    String content = caption != null ? caption : "🖼️ Đã gửi một hình ảnh";
    Message message = new Message(content, sender, receiver);
    message.setMessageType(MessageType.IMAGE);
    message.setImageUrl(imageUrl);

    Message savedMessage = messageRepository.save(message);
    sendWebSocketNotification(savedMessage, receiverId);

    log.info("Image message sent from {} to {}", senderId, receiverId);
    return savedMessage;
  }

  @Override
  @Transactional
  public Message sendVideoMessage(Long senderId, Long receiverId, String videoUrl, String caption) {
    User sender = getUserById(senderId);
    User receiver = getUserById(receiverId);

    String content = caption != null ? caption : "🎥 Đã gửi một video";
    Message message = new Message(content, sender, receiver);
    message.setMessageType(MessageType.VIDEO);
    message.setImageUrl(videoUrl);

    Message savedMessage = messageRepository.save(message);
    sendWebSocketNotification(savedMessage, receiverId);

    log.info("Video message sent from {} to {}", senderId, receiverId);
    return savedMessage;
  }

  @Override
  @Transactional
  public Message sendVoiceMessage(Long senderId, Long receiverId, String audioUrl, Integer duration) {
    User sender = getUserById(senderId);
    User receiver = getUserById(receiverId);

    String content = duration != null ? String.format("🎤 Tin nhắn thoại (%d giây)", duration) : "🎤 Tin nhắn thoại";

    Message message = new Message(content, sender, receiver);
    message.setMessageType(MessageType.VOICE);
    message.setImageUrl(audioUrl);

    Message savedMessage = messageRepository.save(message);
    sendWebSocketNotification(savedMessage, receiverId);

    log.info("Voice message sent from {} to {} (duration: {}s)", senderId, receiverId, duration);
    return savedMessage;
  }

  @Override
  @Transactional
  public Message sendFileMessage(Long senderId, Long receiverId, String fileUrl, String fileName, Long fileSize) {
    User sender = getUserById(senderId);
    User receiver = getUserById(receiverId);

    String content = String.format("📎 %s (%.2f MB)",
        fileName, fileSize != null ? fileSize / (1024.0 * 1024.0) : 0);

    Message message = new Message(content, sender, receiver);
    message.setMessageType(MessageType.FILE);
    message.setImageUrl(fileUrl);

    Message savedMessage = messageRepository.save(message);
    sendWebSocketNotification(savedMessage, receiverId);

    log.info("File message sent from {} to {}: {}", senderId, receiverId, fileName);
    return savedMessage;
  }

  @Override
  @Transactional
  public Message sendLocationMessage(Long senderId, Long receiverId, Double latitude, Double longitude,
      String address) {
    User sender = getUserById(senderId);
    User receiver = getUserById(receiverId);

    String locationData = String.format("%f,%f,%s", latitude, longitude, address != null ? address : "");
    Message message = new Message("📍 Đã chia sẻ vị trí", sender, receiver);
    message.setMessageType(MessageType.LOCATION);
    message.setContent(locationData); // Lưu dữ liệu vị trí vào content

    Message savedMessage = messageRepository.save(message);
    sendWebSocketNotification(savedMessage, receiverId);

    log.info("Location message sent from {} to {}: {},{}", senderId, receiverId, latitude, longitude);
    return savedMessage;
  }

  @Override
  @Transactional
  public Message sendStickerMessage(Long senderId, Long receiverId, String stickerId) {
    User sender = getUserById(senderId);
    User receiver = getUserById(receiverId);

    Message message = new Message("😊 Đã gửi nhãn dán", sender, receiver);
    message.setMessageType(MessageType.STICKER);
    message.setContent(stickerId); // Lưu sticker ID vào content

    Message savedMessage = messageRepository.save(message);
    sendWebSocketNotification(savedMessage, receiverId);

    log.info("Sticker message sent from {} to {}: {}", senderId, receiverId, stickerId);
    return savedMessage;
  }

  @Override
  @Transactional
  public Message sendSystemMessage(Long senderId, Long receiverId, String content) {
    User sender = getUserById(senderId);
    User receiver = getUserById(receiverId);

    Message message = new Message(content, sender, receiver);
    message.setMessageType(MessageType.SYSTEM);

    Message savedMessage = messageRepository.save(message);
    // Không gửi WebSocket notification cho system message

    log.info("System message sent from {} to {}: {}", senderId, receiverId, content);
    return savedMessage;
  }

  @Override
  @Transactional
  public Message sendMessage(Long senderId, Long receiverId, String content, MessageType messageType, String mediaUrl) {
    User sender = getUserById(senderId);
    User receiver = getUserById(receiverId);

    Message message = new Message(content, sender, receiver);
    message.setMessageType(messageType);

    if (mediaUrl != null) {
      message.setImageUrl(mediaUrl);
    }

    Message savedMessage = messageRepository.save(message);

    if (!messageType.isSystem()) {
      sendWebSocketNotification(savedMessage, receiverId);
    }

    log.info("{} message sent from {} to {}", messageType, senderId, receiverId);
    return savedMessage;
  }

  // ============ GET MESSAGE METHODS ============

  @Override
  @Transactional(readOnly = true)
  public List<Message> getMessageHistory(Long currentUserId, Long otherUserId) {
    User currentUser = getUserById(currentUserId);
    User otherUser = getUserById(otherUserId);

    return messageRepository.findMessagesBetweenUsers(currentUser, otherUser, currentUser);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Message> getMessageHistoryWithPagination(Long currentUserId, Long otherUserId, Pageable pageable) {
    User currentUser = getUserById(currentUserId);
    User otherUser = getUserById(otherUserId);

    return messageRepository.findMessagesBetweenUsersWithPagination(currentUser, otherUser, currentUser, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Message getMessageById(Long messageId) {
    return messageRepository.findById(messageId)
        .orElseThrow(() -> new RuntimeException("Message not found with id: " + messageId));
  }

  @Override
  @Transactional(readOnly = true)
  public Message getLastMessage(Long user1Id, Long user2Id) {
    User user1 = getUserById(user1Id);
    User user2 = getUserById(user2Id);

    return messageRepository.findLastMessageBetweenUsers(user1, user2, user1)
        .orElse(null);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Message> getUnreadMessages(Long userId) {
    User user = getUserById(userId);
    return messageRepository.findUnreadMessages(user);
  }

  // ============ MESSAGE STATUS METHODS ============

  @Override
  @Transactional
  public void markMessagesAsRead(Long receiverId, Long senderId) {
    User receiver = getUserById(receiverId);
    User sender = getUserById(senderId);

    messageRepository.markAllMessagesAsRead(receiver, sender);
    log.info("All messages from {} marked as read by {}", senderId, receiverId);
  }

  @Override
  @Transactional
  public void markMessageAsRead(Long messageId, Long userId) {
    Message message = getMessageById(messageId);
    User user = getUserById(userId);

    if (!message.getReceiver().equals(user)) {
      throw new RuntimeException("User is not the receiver of this message");
    }

    message.setIsRead(true);
    messageRepository.save(message);
    log.info("Message {} marked as read by user {}", messageId, userId);
  }

  @Override
  @Transactional(readOnly = true)
  public Long getUnreadMessageCount(Long receiverId, Long senderId) {
    User receiver = getUserById(receiverId);
    User sender = getUserById(senderId);

    return messageRepository.countUnreadMessagesFromUser(receiver, sender);
  }

  @Override
  @Transactional(readOnly = true)
  public Long getTotalUnreadMessageCount(Long userId) {
    User user = getUserById(userId);
    return messageRepository.countTotalUnreadMessages(user);
  }

  // ============ MESSAGE MANAGEMENT METHODS ============

  @Override
  @Transactional
  public void deleteMessageForUser(Long messageId, Long userId) {
    User user = getUserById(userId);
    Message message = getMessageById(messageId);

    if (message.getSender().equals(user)) {
      messageRepository.softDeleteForSender(messageId, user);
    } else if (message.getReceiver().equals(user)) {
      messageRepository.softDeleteForReceiver(messageId, user);
    } else {
      throw new RuntimeException("User not authorized to delete this message");
    }

    log.info("Message {} deleted for user {}", messageId, userId);
  }

  @Override
  @Transactional
  public void recallMessage(Long messageId, Long senderId) {
    User sender = getUserById(senderId);
    Message message = getMessageById(messageId);

    if (!message.getSender().equals(sender)) {
      throw new RuntimeException("Only sender can recall message");
    }

    if (message.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
      throw new RuntimeException("Cannot recall message after 5 minutes");
    }

    messageRepository.softDeleteForSender(messageId, sender);
    messageRepository.softDeleteForReceiver(messageId, message.getReceiver());

    // Gửi notification về việc thu hồi tin nhắn
    sendRecallNotification(message, message.getReceiver().getId());
    log.info("Message {} recalled by sender {}", messageId, senderId);
  }

  @Override
  @Transactional
  public Message forwardMessage(Long messageId, Long senderId, List<Long> receiverIds) {
    User sender = getUserById(senderId);
    Message originalMessage = getMessageById(messageId);

    Message firstForwardedMessage = null;

    for (Long receiverId : receiverIds) {
      User receiver = getUserById(receiverId);
      Message forwardedMessage = createForwardedMessage(originalMessage, sender, receiver);
      Message savedMessage = messageRepository.save(forwardedMessage);

      if (firstForwardedMessage == null) {
        firstForwardedMessage = savedMessage;
      }

      sendWebSocketNotification(savedMessage, receiverId);
    }

    log.info("Message {} forwarded by {} to {} receivers", messageId, senderId, receiverIds.size());
    return firstForwardedMessage != null ? firstForwardedMessage : originalMessage;
  }

  @Override
  @Transactional
  public Message copyMessage(Long messageId, Long senderId, Long receiverId) {
    User sender = getUserById(senderId);
    User receiver = getUserById(receiverId);
    Message originalMessage = getMessageById(messageId);

    Message copiedMessage = createCopiedMessage(originalMessage, sender, receiver);
    Message savedMessage = messageRepository.save(copiedMessage);

    sendWebSocketNotification(savedMessage, receiverId);
    log.info("Message {} copied by {} to {}", messageId, senderId, receiverId);
    return savedMessage;
  }

  // ============ MESSAGE SEARCH METHODS ============

  @Override
  @Transactional(readOnly = true)
  public Page<Message> searchMessages(Long userId, String keyword, Pageable pageable) {
    User user = getUserById(userId);
    return messageRepository.searchMessagesByContent(user, keyword, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Message> findMediaMessages(Long currentUserId, Long otherUserId, MessageType mediaType) {
    User currentUser = getUserById(currentUserId);
    User otherUser = getUserById(otherUserId);

    return messageRepository.findMediaMessagesBetweenUsers(currentUser, otherUser, currentUser, mediaType);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Message> findFileMessages(Long currentUserId, Long otherUserId) {
    User currentUser = getUserById(currentUserId);
    User otherUser = getUserById(otherUserId);

    return messageRepository.findFileMessagesBetweenUsers(currentUser, otherUser, currentUser);
  }

  // ============ PRIVATE HELPER METHODS ============

  private User getUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
  }

  private Message createForwardedMessage(Message original, User newSender, User newReceiver) {
    Message forwarded = new Message();
    forwarded.setContent("[Chuyển tiếp] " + original.getContent());
    forwarded.setSender(newSender);
    forwarded.setReceiver(newReceiver);
    forwarded.setMessageType(original.getMessageType());
    forwarded.setImageUrl(original.getImageUrl());
    return forwarded;
  }

  private Message createCopiedMessage(Message original, User newSender, User newReceiver) {
    Message copied = new Message();
    copied.setContent(original.getContent());
    copied.setSender(newSender);
    copied.setReceiver(newReceiver);
    copied.setMessageType(original.getMessageType());
    copied.setImageUrl(original.getImageUrl());
    return copied;
  }

  /**
   * Gửi notification qua WebSocket
   */
  private void sendWebSocketNotification(Message message, Long receiverId) {
    try {
      Map<String, Object> messageDTO = createMessageDTO(message);

      // Gửi đến receiver
      messagingTemplate.convertAndSendToUser(
          receiverId.toString(),
          "/queue/messages",
          messageDTO);

      log.debug("WebSocket notification sent to user {}", receiverId);
    } catch (Exception e) {
      log.error("Failed to send WebSocket notification: {}", e.getMessage());
    }
  }

  /**
   * Gửi notification thu hồi tin nhắn
   */
  private void sendRecallNotification(Message message, Long receiverId) {
    try {
      Map<String, Object> recallNotification = new HashMap<>();
      recallNotification.put("type", "MESSAGE_RECALLED");
      recallNotification.put("messageId", message.getId());
      recallNotification.put("recalledAt", LocalDateTime.now());

      messagingTemplate.convertAndSendToUser(
          receiverId.toString(),
          "/queue/messages",
          recallNotification);
      log.debug("Recall notification sent to user {}", receiverId);
    } catch (Exception e) {
      log.error("Failed to send recall notification: {}", e.getMessage());
    }
  }

  /**
   * Tạo DTO cho WebSocket message
   */
  private Map<String, Object> createMessageDTO(Message message) {
    Map<String, Object> dto = new HashMap<>();
    dto.put("id", message.getId());
    dto.put("content", message.getContent());
    dto.put("imageUrl", message.getImageUrl());
    dto.put("messageType", message.getMessageType().name());
    dto.put("senderId", message.getSender().getId());
    dto.put("senderName", message.getSender().getUsername());
    dto.put("senderAvatar", message.getSender().getProfilePicture());
    dto.put("receiverId", message.getReceiver().getId());
    dto.put("isRead", message.getIsRead());
    dto.put("createdAt", message.getCreatedAt());
    dto.put("icon", message.getMessageType().getIcon());
    dto.put("preview", getMessagePreview(message));
    dto.put("isDeletable", !message.getMessageType().isSystem());

    return dto;
  }

  /**
   * Tạo preview message cho frontend
   */
  private String getMessagePreview(Message message) {
    switch (message.getMessageType()) {
      case TEXT:
        return message.getContent().length() > 50
            ? message.getContent().substring(0, 50) + "..."
            : message.getContent();
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
        return "⚙️ " + message.getContent();
      default:
        return message.getContent();
    }
  }
}
