import apiClient from './api';

class MessageService {
  // ============ SEND MESSAGE METHODS ============
  
  async sendTextMessage(receiverId, content) {
    try {
      const response = await apiClient.post('/messages/send/text', {
        receiverId,
        content
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to send message');
    }
  }

  async sendImageMessage(receiverId, mediaUrl, caption = null) {
    try {
      const response = await apiClient.post('/messages/send/image', {
        receiverId,
        mediaUrl,
        caption
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to send image');
    }
  }

  async sendVideoMessage(receiverId, mediaUrl, caption = null) {
    try {
      const response = await apiClient.post('/messages/send/video', {
        receiverId,
        mediaUrl,
        caption
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to send video');
    }
  }

  async sendVoiceMessage(receiverId, audioUrl, duration = null) {
    try {
      const response = await apiClient.post('/messages/send/voice', {
        receiverId,
        audioUrl,
        duration
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to send voice message');
    }
  }

  async sendFileMessage(receiverId, fileUrl, fileName, fileSize = null) {
    try {
      const response = await apiClient.post('/messages/send/file', {
        receiverId,
        fileUrl,
        fileName,
        fileSize
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to send file');
    }
  }

  async sendLocationMessage(receiverId, latitude, longitude, address = null) {
    try {
      const response = await apiClient.post('/messages/send/location', {
        receiverId,
        latitude,
        longitude,
        address
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to send location');
    }
  }

  async sendStickerMessage(receiverId, stickerId) {
    try {
      const response = await apiClient.post('/messages/send/sticker', {
        receiverId,
        stickerId
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to send sticker');
    }
  }

  // ============ GET MESSAGE METHODS ============

  async getMessageHistory(otherUserId) {
    try {
      const response = await apiClient.get(`/messages/history/${otherUserId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to get message history');
    }
  }

  async getMessageHistoryWithPagination(otherUserId, page = 0, size = 20) {
    try {
      const response = await apiClient.get(`/messages/history/${otherUserId}/page?page=${page}&size=${size}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to get message history');
    }
  }

  async getUnreadMessages() {
    try {
      const response = await apiClient.get('/messages/unread');
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to get unread messages');
    }
  }

  async getUnreadMessageCount(senderId = null) {
    try {
      const url = senderId 
        ? `/messages/unread/count?senderId=${senderId}`
        : '/messages/unread/count';
      const response = await apiClient.get(url);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to get unread count');
    }
  }

  // ============ MESSAGE MANAGEMENT ============

  async markMessagesAsRead(senderId) {
    try {
      const response = await apiClient.put(`/messages/mark-read/${senderId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to mark messages as read');
    }
  }

  async deleteMessage(messageId) {
    try {
      const response = await apiClient.delete(`/messages/${messageId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to delete message');
    }
  }

  async recallMessage(messageId) {
    try {
      const response = await apiClient.put(`/messages/recall/${messageId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to recall message');
    }
  }

  async forwardMessage(messageId, receiverIds) {
    try {
      const response = await apiClient.post('/messages/forward', {
        messageId,
        receiverIds
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to forward message');
    }
  }

  async copyMessage(messageId, receiverId) {
    try {
      const response = await apiClient.post('/messages/copy', {
        messageId,
        receiverId
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to copy message');
    }
  }

  // ============ SEARCH MESSAGES ============

  async searchMessages(keyword, page = 0, size = 20) {
    try {
      const response = await apiClient.get(`/messages/search?keyword=${keyword}&page=${page}&size=${size}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Failed to search messages');
    }
  }
}

export default new MessageService();
