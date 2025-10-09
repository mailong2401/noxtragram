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

  // ============ BASIC MESSAGE QUERIES ============

  @Query("SELECT m FROM Message m WHERE " +
      "((m.sender = :user1 AND m.receiver = :user2) OR " +
      "(m.sender = :user2 AND m.receiver = :user1)) AND " +
      "((m.sender = :currentUser AND m.isDeletedForSender = false) OR " +
      "(m.receiver = :currentUser AND m.isDeletedForReceiver = false)) " +
      "ORDER BY m.createdAt ASC")
  List<Message> findMessagesBetweenUsers(@Param("user1") User user1,
      @Param("user2") User user2,
      @Param("currentUser") User currentUser);

  @Query("SELECT m FROM Message m WHERE " +
      "((m.sender = :user1 AND m.receiver = :user2) OR " +
      "(m.sender = :user2 AND m.receiver = :user1)) AND " +
      "((m.sender = :currentUser AND m.isDeletedForSender = false) OR " +
      "(m.receiver = :currentUser AND m.isDeletedForReceiver = false)) " +
      "ORDER BY m.createdAt DESC")
  Page<Message> findMessagesBetweenUsersWithPagination(@Param("user1") User user1,
      @Param("user2") User user2,
      @Param("currentUser") User currentUser,
      Pageable pageable);

  // ============ MESSAGE STATUS QUERIES ============

  @Query("SELECT COUNT(m) FROM Message m WHERE " +
      "m.receiver = :receiver AND " +
      "m.sender = :sender AND " +
      "m.isRead = false AND " +
      "m.isDeletedForReceiver = false")
  Long countUnreadMessagesFromUser(@Param("receiver") User receiver,
      @Param("sender") User sender);

  @Query("SELECT COUNT(m) FROM Message m WHERE " +
      "m.receiver = :receiver AND " +
      "m.isRead = false AND " +
      "m.isDeletedForReceiver = false")
  Long countTotalUnreadMessages(@Param("receiver") User receiver);

  @Modifying
  @Query("UPDATE Message m SET m.isRead = true WHERE " +
      "m.receiver = :receiver AND " +
      "m.sender = :sender AND " +
      "m.isRead = false")
  void markAllMessagesAsRead(@Param("receiver") User receiver,
      @Param("sender") User sender);

  // ============ MESSAGE SEARCH QUERIES ============

  @Query("SELECT m FROM Message m WHERE " +
      "((m.sender = :currentUser AND m.isDeletedForSender = false) OR " +
      "(m.receiver = :currentUser AND m.isDeletedForReceiver = false)) AND " +
      "(LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
      "ORDER BY m.createdAt DESC")
  Page<Message> searchMessagesByContent(@Param("currentUser") User currentUser,
      @Param("keyword") String keyword,
      Pageable pageable);

  @Query("SELECT m FROM Message m WHERE " +
      "((m.sender = :user1 AND m.receiver = :user2) OR " +
      "(m.sender = :user2 AND m.receiver = :user1)) AND " +
      "((m.sender = :currentUser AND m.isDeletedForSender = false) OR " +
      "(m.receiver = :currentUser AND m.isDeletedForReceiver = false)) AND " +
      "m.messageType = :mediaType " +
      "ORDER BY m.createdAt DESC")
  List<Message> findMediaMessagesBetweenUsers(@Param("user1") User user1,
      @Param("user2") User user2,
      @Param("currentUser") User currentUser,
      @Param("mediaType") MessageType mediaType);

  @Query("SELECT m FROM Message m WHERE " +
      "((m.sender = :user1 AND m.receiver = :user2) OR " +
      "(m.sender = :user2 AND m.receiver = :user1)) AND " +
      "((m.sender = :currentUser AND m.isDeletedForSender = false) OR " +
      "(m.receiver = :currentUser AND m.isDeletedForReceiver = false)) AND " +
      "m.messageType = com.noxtragram.model.entity.MessageType.FILE " +
      "ORDER BY m.createdAt DESC")
  List<Message> findFileMessagesBetweenUsers(@Param("user1") User user1,
      @Param("user2") User user2,
      @Param("currentUser") User currentUser);

  // ============ SPECIAL MESSAGE QUERIES ============

  @Query("SELECT m FROM Message m WHERE " +
      "((m.sender = :user1 AND m.receiver = :user2) OR " +
      "(m.sender = :user2 AND m.receiver = :user1)) AND " +
      "((m.sender = :currentUser AND m.isDeletedForSender = false) OR " +
      "(m.receiver = :currentUser AND m.isDeletedForReceiver = false)) " +
      "ORDER BY m.createdAt DESC LIMIT 1")
  Optional<Message> findLastMessageBetweenUsers(@Param("user1") User user1,
      @Param("user2") User user2,
      @Param("currentUser") User currentUser);

  @Query("SELECT m FROM Message m WHERE " +
      "m.receiver = :receiver AND " +
      "m.isRead = false AND " +
      "m.isDeletedForReceiver = false " +
      "ORDER BY m.createdAt ASC")
  List<Message> findUnreadMessages(@Param("receiver") User receiver);

  // ============ DELETE OPERATIONS ============

  @Modifying
  @Query("UPDATE Message m SET m.isDeletedForSender = true WHERE m.id = :messageId AND m.sender = :user")
  void softDeleteForSender(@Param("messageId") Long messageId, @Param("user") User user);

  @Modifying
  @Query("UPDATE Message m SET m.isDeletedForReceiver = true WHERE m.id = :messageId AND m.receiver = :user")
  void softDeleteForReceiver(@Param("messageId") Long messageId, @Param("user") User user);

  // ============ CHAT ROOM QUERIES ============

  @Query("SELECT m FROM Message m WHERE " +
      "m.chatRoom.id = :chatRoomId AND " +
      "((m.sender = :currentUser AND m.isDeletedForSender = false) OR " +
      "(m.receiver = :currentUser AND m.isDeletedForReceiver = false)) " +
      "ORDER BY m.createdAt ASC")
  List<Message> findByChatRoomId(@Param("chatRoomId") Long chatRoomId,
      @Param("currentUser") User currentUser);

  // ============ MESSAGE STATISTICS ============

  @Query("SELECT COUNT(m) FROM Message m WHERE " +
      "((m.sender = :user1 AND m.receiver = :user2) OR " +
      "(m.sender = :user2 AND m.receiver = :user1)) AND " +
      "((m.sender = :currentUser AND m.isDeletedForSender = false) OR " +
      "(m.receiver = :currentUser AND m.isDeletedForReceiver = false))")
  Long countMessagesBetweenUsers(@Param("user1") User user1,
      @Param("user2") User user2,
      @Param("currentUser") User currentUser);

  @Query("SELECT m.messageType, COUNT(m) FROM Message m WHERE " +
      "((m.sender = :user1 AND m.receiver = :user2) OR " +
      "(m.sender = :user2 AND m.receiver = :user1)) AND " +
      "((m.sender = :currentUser AND m.isDeletedForSender = false) OR " +
      "(m.receiver = :currentUser AND m.isDeletedForReceiver = false)) " +
      "GROUP BY m.messageType")
  List<Object[]> countMessagesByTypeBetweenUsers(@Param("user1") User user1,
      @Param("user2") User user2,
      @Param("currentUser") User currentUser);
}
