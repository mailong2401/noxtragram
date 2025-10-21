import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

class ChatService {
  constructor() {
    this.stompClient = null;
    this.subscriptions = new Map();
    this.messageCallbacks = new Set();
    this.typingCallbacks = new Set();
    this.readReceiptCallbacks = new Set();
    this.isConnected = false;
  }

  connect(userId, onConnect, onError) {
    if (this.isConnected) return;

    const socket = new SockJS('http://localhost:8080/api/messages'); // match server endpoint
    this.stompClient = Stomp.over(socket);

    // Bật debug để log mọi frame
    this.stompClient.debug = (msg) => console.log('[STOMP DEBUG]', msg);

    this.stompClient.connect(
      { userId },
      (frame) => {
        console.log('Connected: ' + frame);
        this.isConnected = true;

        this.subscribeToUserMessages(userId);

        if (onConnect) onConnect();
      },
      (error) => {
        console.error('WebSocket connection error:', error);
        this.isConnected = false;
        if (onError) onError(error);
      }
    );
  }

  subscribeToUserMessages(userId) {
    // subscribe theo user queue
    const subUser = this.stompClient.subscribe('/user/queue/messages', (msg) => {
      console.log('[WS RECEIVED /user/queue/messages]', msg?.body);
      try {
        const data = JSON.parse(msg.body);
        this.notifyMessageCallbacks(data);
      } catch (e) {
        console.error('Failed parse ws message', e);
      }
    });
    this.subscriptions.set('user-messages', subUser);

    // fallback: topic chung
    const subTopic = this.stompClient.subscribe('/topic/messages', (msg) => {
      console.log('[WS RECEIVED /topic/messages]', msg?.body);
      try {
        const data = JSON.parse(msg.body);
        this.notifyMessageCallbacks(data);
      } catch (e) { console.error(e); }
    });
    this.subscriptions.set('topic-messages', subTopic);
  }

  sendMessage(receiverId, content, messageType = 'TEXT', mediaUrl = null) {
    if (!this.isConnected || !this.stompClient) {
      console.error('WebSocket not connected');
      return;
    }

    this.stompClient.send(
      '/app/chat.send',
      {},
      JSON.stringify({ receiverId, content, messageType, mediaUrl })
    );
  }

  sendTypingIndicator(receiverId, isTyping) {
    if (!this.isConnected || !this.stompClient) return;

    this.stompClient.send(
      '/app/chat.typing',
      {},
      JSON.stringify({ receiverId, isTyping })
    );
  }

  sendReadReceipt(senderId) {
    if (!this.isConnected || !this.stompClient) return;

    this.stompClient.send(
      '/app/chat.read-receipt',
      {},
      JSON.stringify({ senderId })
    );
  }

  // callback
  onMessage(callback) { this.messageCallbacks.add(callback); }
  offMessage(callback) { this.messageCallbacks.delete(callback); }
  onTyping(callback) { this.typingCallbacks.add(callback); }
  offTyping(callback) { this.typingCallbacks.delete(callback); }
  onReadReceipt(callback) { this.readReceiptCallbacks.add(callback); }
  offReadReceipt(callback) { this.readReceiptCallbacks.delete(callback); }

  notifyMessageCallbacks(message) {
    this.messageCallbacks.forEach(cb => {
      try { cb(message); } catch (err) { console.error('Error in message callback', err); }
    });
  }

  disconnect() {
    if (this.stompClient) {
      this.subscriptions.forEach(sub => sub.unsubscribe());
      this.subscriptions.clear();
      this.stompClient.disconnect();
      this.stompClient = null;
    }

    this.isConnected = false;
    this.messageCallbacks.clear();
    this.typingCallbacks.clear();
    this.readReceiptCallbacks.clear();

    console.log('WebSocket disconnected');
  }

  getConnectionStatus() {
    return this.isConnected;
  }
}

export default new ChatService();

