package com.noxtragram.repository;

import com.noxtragram.model.entity.Message;
import com.noxtragram.model.entity.MessageType;
import com.noxtragram.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

  // 📨 Tìm tin nhắn giữa 2 user
  @Query("SELECT m FROM Message m WHERE " +
      "((m.sender = :user1 AND m.receiver = :user2) OR " +
      "(m.sender = :user2 AND m.receiver = :user1)) AND " +
      "m.isDeletedForSender = false AND " +
      "(m.receiver = :currentUser OR m.isDeletedForReceiver = false) " +
      "ORDER BY m.createdAt ASC")
  List<Message> findMessagesBetweenUsers(@Param("user1") User user1,
      @Param("user2") User user2,
      @Param("currentUser") User currentUser);

  // 📄 Tìm tin nhắn giữa 2 user với phân trang
  @Query("SELECT m FROM Message m WHERE " +
      "((m.sender = :user1 AND m.receiver = :user2) OR " +
      "(m.sender = :user2 AND m.receiver = :user1)) AND " +
      "m.isDeletedForSender = false AND " +
      "(m.receiver = :currentUser OR m.isDeletedForReceiver = false) " +
      "ORDER BY m.createdAt DESC")
  Page<Message> findMessagesBetweenUsersWithPagination(@Param("user1") User user1,
      @Param("user2") User user2,
      @Param("currentUser") User currentUser,
      Pageable pageable);

  // 🔍 Tìm tin nhắn cuối cùng giữa 2 user
  @Query("SELECT m FROM Message m WHERE " +
      "((m.sender = :user1 AND m.receiver = :user2) OR " +
      "(m.sender = :user2 AND m.receiver = :user1)) AND " +
      "m.isDeletedForSender = false AND " +
      "(m.receiver = :currentUser OR m.isDeletedForReceiver = false) " +
      "ORDER BY m.createdAt DESC LIMIT 1")
  Optional<Message> findLastMessageBetweenUsers(@Param("user1") User user1,
      @Param("user2") User user2,
      @Param("currentUser") User currentUser);

  // 📊 Đếm tin nhắn chưa đọc từ một user cụ thể
  @Query("SELECT COUNT(m) FROM Message m WHERE " +
      "m.receiver = :receiver AND " +
      "m.sender = :sender AND " +
      "m.isRead = false AND " +
      "m.isDeletedForReceiver = false")
  Long countUnreadMessagesFromUser(@Param("receiver") User receiver,
      @Param("sender") User sender);

  // 📈 Đếm tổng số tin nhắn chưa đọc
  @Query("SELECT COUNT(m) FROM Message m WHERE " +
      "m.receiver = :receiver AND " +
      "m.isRead = false AND " +
      "m.isDeletedForReceiver = false")
  Long countTotalUnreadMessages(@Param("receiver") User receiver);

  // 📋 Lấy tin nhắn chưa đọc
  @Query("SELECT m FROM Message m WHERE " +
      "m.receiver = :receiver AND " +
      "m.isRead = false AND " +
      "m.isDeletedForReceiver = false " +
      "ORDER BY m.createdAt ASC")
  List<Message> findUnreadMessages(@Param("receiver") User receiver);

  // ✅ Đánh dấu tất cả tin nhắn là đã đọc
  @Modifying
  @Query("UPDATE Message m SET m.isRead = true WHERE " +
      "m.receiver = :receiver AND " +
      "m.sender = :sender AND " +
      "m.isRead = false")
  void markAllMessagesAsRead(@Param("receiver") User receiver,
      @Param("sender") User sender);

  // 🗑️ Soft delete cho sender
  @Modifying
  @Query("UPDATE Message m SET m.isDeletedForSender = true WHERE m.id = :messageId AND m.sender = :user")
  void softDeleteForSender(@Param("messageId") Long messageId,
      @Param("user") User user);

  // 🗑️ Soft delete cho receiver
  @Modifying
  @Query("UPDATE Message m SET m.isDeletedForReceiver = true WHERE m.id = :messageId AND m.receiver = :user")
  void softDeleteForReceiver(@Param("messageId") Long messageId,
      @Param("user") User user);

  // 🔍 Tìm kiếm tin nhắn theo nội dung
  @Query("SELECT m FROM Message m WHERE " +
      "(m.sender = :user OR m.receiver = :user) AND " +
      "((m.sender = :user AND m.isDeletedForSender = false) OR " +
      "(m.receiver = :user AND m.isDeletedForReceiver = false)) AND " +
      "LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
      "ORDER BY m.createdAt DESC")
  Page<Message> searchMessagesByContent(@Param("user") User user,
      @Param("keyword") String keyword,
      Pageable pageable);

  // 🖼️ Lấy tin nhắn media (hình ảnh, video, voice)
  @Query("SELECT m FROM Message m WHERE " +
      "((m.sender = :user1 AND m.receiver = :user2) OR " +
      "(m.sender = :user2 AND m.receiver = :user1)) AND " +
      "m.isDeletedForSender = false AND " +
      "(m.receiver = :currentUser OR m.isDeletedForReceiver = false) AND " +
      "m.messageType = :mediaType " +
      "ORDER BY m.createdAt DESC")
  List<Message> findMediaMessagesBetweenUsers(@Param("user1") User user1,
      @Param("user2") User user2,
      @Param("currentUser") User currentUser,
      @Param("mediaType") MessageType mediaType);

  // 📎 Lấy tin nhắn file
  @Query("SELECT m FROM Message m WHERE " +
      "((m.sender = :user1 AND m.receiver = :user2) OR " +
      "(m.sender = :user2 AND m.receiver = :user1)) AND " +
      "m.isDeletedForSender = false AND " +
      "(m.receiver = :currentUser OR m.isDeletedForReceiver = false) AND " +
      "m.messageType = 'FILE' " +
      "ORDER BY m.createdAt DESC")
  List<Message> findFileMessagesBetweenUsers(@Param("user1") User user1,
      @Param("user2") User user2,
      @Param("currentUser") User currentUser);
}
