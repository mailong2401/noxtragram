import { useState, useEffect, useCallback, useRef } from 'react';
import messageService from '../services/messageService';
import chatService from '../services/chatService';
import { useAuth } from '../contexts/AuthContext';

export const useMessages = (otherUserId) => {
  const { user } = useAuth();
  const [messages, setMessages] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [hasMore, setHasMore] = useState(true);
  const [page, setPage] = useState(0);

  // Ref lưu messages mới nhất để tránh closure stale
  const messagesRef = useRef([]);
  messagesRef.current = messages;

  const loadMessages = useCallback(async (reset = false) => {
    if (!otherUserId) return;
    try {
      setIsLoading(true);
      setError(null);

      const currentPage = reset ? 0 : page;
      const response = await messageService.getMessageHistoryWithPagination(otherUserId, currentPage);
      const responseData = response.content || response.data?.content || [];

      if (reset) setMessages(responseData.reverse());
      else setMessages(prev => [...responseData.reverse(), ...prev]);

      setHasMore(currentPage + 1 < (response.totalPages || 1));
      setPage(currentPage + 1);
    } catch (err) {
      console.error('Failed to load messages:', err);
      setError(err.message || 'Error loading messages');
    } finally { setIsLoading(false); }
  }, [otherUserId, page]);

  const sendTextMessage = useCallback(async (content) => {
    if (!otherUserId || !content.trim()) return;
    try {
      const response = await messageService.sendTextMessage(otherUserId, content);
      if (response.success) {
        const message = response.data;
        setMessages(prev => [...prev, message]);
        chatService.sendMessage(otherUserId, content, 'TEXT');
        return message;
      }
    } catch (err) {
      console.error('Failed to send message:', err);
      setError(err.message);
      throw err;
    }
  }, [otherUserId]);

  const markAsRead = useCallback(async () => {
    if (!otherUserId) return;
    try {
      await messageService.markMessagesAsRead(otherUserId);
      setMessages(prev => prev.map(msg => ({ ...msg, isRead: true })));
      chatService.sendReadReceipt(otherUserId);
    } catch (err) {
      console.error('Failed to mark messages as read:', err);
    }
  }, [otherUserId]);

  // WebSocket listener
  useEffect(() => {
    const handleNewMessage = (message) => {
      if (!message) return;

      const senderId = message.senderId ?? message.sender?.id;
      const receiverId = message.receiverId ?? message.receiver?.id;

      if (senderId === otherUserId || receiverId === otherUserId) {
        setMessages(prev => [...prev, message]); // luôn dùng prev
        if (senderId === otherUserId) markAsRead();
      }
    };

    chatService.onMessage(handleNewMessage);
    return () => chatService.offMessage(handleNewMessage);
  }, [otherUserId, markAsRead]);

  // Load initial messages khi otherUserId thay đổi
  useEffect(() => {
    if (otherUserId) {
      setMessages([]);
      setPage(0);
      setHasMore(true);
      loadMessages(true);
      markAsRead();
    }
  }, [otherUserId, loadMessages, markAsRead]);

  const loadMore = useCallback(() => {
    if (!isLoading && hasMore) loadMessages(false);
  }, [isLoading, hasMore, loadMessages]);

  return {
    messages,
    isLoading,
    error,
    hasMore,
    loadMore,
    sendTextMessage,
    markAsRead
  };
};

