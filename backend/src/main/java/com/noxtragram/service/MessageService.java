package com.noxtragram.service;

import com.noxtragram.model.entity.Message;
import com.noxtragram.model.entity.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageService {

  // Send methods
  Message sendTextMessage(Long senderId, Long receiverId, String content);

  Message sendImageMessage(Long senderId, Long receiverId, String imageUrl, String caption);

  Message sendVideoMessage(Long senderId, Long receiverId, String videoUrl, String caption);

  Message sendVoiceMessage(Long senderId, Long receiverId, String audioUrl, Integer duration);

  Message sendFileMessage(Long senderId, Long receiverId, String fileUrl, String fileName, Long fileSize);

  Message sendLocationMessage(Long senderId, Long receiverId, Double latitude, Double longitude, String address);

  Message sendStickerMessage(Long senderId, Long receiverId, String stickerId);

  Message sendSystemMessage(Long senderId, Long receiverId, String content);

  Message sendMessage(Long senderId, Long receiverId, String content, MessageType messageType, String mediaUrl);

  // Get methods
  List<Message> getMessageHistory(Long currentUserId, Long otherUserId);

  Page<Message> getMessageHistoryWithPagination(Long currentUserId, Long otherUserId, Pageable pageable);

  Message getMessageById(Long messageId);

  Message getLastMessage(Long user1Id, Long user2Id);

  List<Message> getUnreadMessages(Long userId);

  // Status methods
  void markMessagesAsRead(Long receiverId, Long senderId);

  void markMessageAsRead(Long messageId, Long userId);

  Long getUnreadMessageCount(Long receiverId, Long senderId);

  Long getTotalUnreadMessageCount(Long userId);

  // Management methods
  void deleteMessageForUser(Long messageId, Long userId);

  void recallMessage(Long messageId, Long senderId);

  Message forwardMessage(Long messageId, Long senderId, List<Long> receiverIds);

  Message copyMessage(Long messageId, Long senderId, Long receiverId);

  // Search methods
  Page<Message> searchMessages(Long userId, String keyword, Pageable pageable);

  List<Message> findMediaMessages(Long currentUserId, Long otherUserId, MessageType mediaType);

  List<Message> findFileMessages(Long currentUserId, Long otherUserId);
}
