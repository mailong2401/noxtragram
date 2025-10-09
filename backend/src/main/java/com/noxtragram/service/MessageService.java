package com.noxtragram.service;

import com.noxtragram.model.entity.Message;
import com.noxtragram.model.entity.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageService {

  // ============ SEND MESSAGE METHODS ============

  /**
   * Gửi tin nhắn văn bản
   */
  Message sendTextMessage(Long senderId, Long receiverId, String content);

  /**
   * Gửi tin nhắn hình ảnh
   */
  Message sendImageMessage(Long senderId, Long receiverId, String imageUrl, String caption);

  /**
   * Gửi tin nhắn video
   */
  Message sendVideoMessage(Long senderId, Long receiverId, String videoUrl, String caption);

  /**
   * Gửi tin nhắn thoại
   */
  Message sendVoiceMessage(Long senderId, Long receiverId, String audioUrl, Integer duration);

  /**
   * Gửi tin nhắn file
   */
  Message sendFileMessage(Long senderId, Long receiverId, String fileUrl, String fileName, Long fileSize);

  /**
   * Gửi tin nhắn vị trí
   */
  Message sendLocationMessage(Long senderId, Long receiverId, Double latitude, Double longitude, String address);

  /**
   * Gửi tin nhắn sticker
   */
  Message sendStickerMessage(Long senderId, Long receiverId, String stickerId);

  /**
   * Gửi tin nhắn hệ thống
   */
  Message sendSystemMessage(Long senderId, Long receiverId, String content);

  /**
   * Gửi tin nhắn với loại tùy chỉnh
   */
  Message sendMessage(Long senderId, Long receiverId, String content, MessageType messageType, String mediaUrl);

  // ============ GET MESSAGE METHODS ============

  /**
   * Lấy lịch sử tin nhắn giữa 2 user
   */
  List<Message> getMessageHistory(Long currentUserId, Long otherUserId);

  /**
   * Lấy lịch sử tin nhắn với phân trang
   */
  Page<Message> getMessageHistoryWithPagination(Long currentUserId, Long otherUserId, Pageable pageable);

  /**
   * Lấy tin nhắn theo ID
   */
  Message getMessageById(Long messageId);

  /**
   * Lấy tin nhắn cuối cùng giữa 2 user
   */
  Message getLastMessage(Long user1Id, Long user2Id);

  /**
   * Lấy tất cả tin nhắn chưa đọc của user
   */
  List<Message> getUnreadMessages(Long userId);

  /**
   * Lấy tin nhắn theo chat room
   */
  List<Message> getMessagesByChatRoom(Long chatRoomId, Long currentUserId);

  // ============ MESSAGE STATUS METHODS ============

  /**
   * Đánh dấu tin nhắn đã đọc
   */
  void markMessagesAsRead(Long receiverId, Long senderId);

  /**
   * Đánh dấu một tin nhắn cụ thể đã đọc
   */
  void markMessageAsRead(Long messageId, Long userId);

  /**
   * Đếm tin nhắn chưa đọc từ một user
   */
  Long getUnreadMessageCount(Long receiverId, Long senderId);

  /**
   * Đếm tổng tin nhắn chưa đọc của user
   */
  Long getTotalUnreadMessageCount(Long userId);

  // ============ MESSAGE MANAGEMENT METHODS ============

  /**
   * Xóa tin nhắn (soft delete)
   */
  void deleteMessageForUser(Long messageId, Long userId);

  /**
   * Thu hồi tin nhắn (xóa cho cả 2)
   */
  void recallMessage(Long messageId, Long senderId);

  /**
   * Chuyển tiếp tin nhắn
   */
  Message forwardMessage(Long messageId, Long senderId, List<Long> receiverIds);

  /**
   * Sao chép tin nhắn
   */
  Message copyMessage(Long messageId, Long senderId, Long receiverId);

  // ============ MESSAGE SEARCH METHODS ============

  /**
   * Tìm kiếm tin nhắn theo nội dung
   */
  Page<Message> searchMessages(Long userId, String keyword, Pageable pageable);

  /**
   * Tìm kiếm tin nhắn media
   */
  List<Message> findMediaMessages(Long currentUserId, Long otherUserId, MessageType mediaType);

  /**
   * Tìm kiếm tin nhắn file
   */
  List<Message> findFileMessages(Long currentUserId, Long otherUserId);
}
