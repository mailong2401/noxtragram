import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { useMessages } from '../../hooks/useMessages';
import chatService from '../../services/chatService';
import userService from '../../services/userService';
import messageService from '../../services/messageService';
import styles from './Messages.module.css';

const Messages = () => {
  const { user, isAuthenticated } = useAuth();
  const { conversationId } = useParams();
  const [conversations, setConversations] = useState([]);
  const [messageableUsers, setMessageableUsers] = useState([]);
  const [activeConversation, setActiveConversation] = useState(null);
  const [newMessage, setNewMessage] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [isSending, setIsSending] = useState(false);
  const [isTyping, setIsTyping] = useState(false);
  const [isLoadingConversations, setIsLoadingConversations] = useState(false);
  const [isLoadingUsers, setIsLoadingUsers] = useState(false);
  const [showNewChatModal, setShowNewChatModal] = useState(false);
  const messagesEndRef = useRef(null);
  const navigate = useNavigate();

  // ------------------ HOOK useMessages ------------------
  const {
    messages,
    isLoading,
    error,
    hasMore,
    sendTextMessage,
    loadMore,
    markAsRead
  } = useMessages(activeConversation?.id);

  // Scroll xuống dưới mỗi khi có message mới
  const scrollToBottom = useCallback(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, []);

  useEffect(() => {
    scrollToBottom();
  }, [messages, scrollToBottom]);

  // ------------------ Load Messageable Users ------------------
  const loadMessageableUsers = useCallback(async () => {
    if (!isAuthenticated) return [];
    setIsLoadingUsers(true);
    try {
      const response = await userService.getMessageableUsers(0, 100);
      const users = response.success ? (response.data?.content || response.data || []) : getFallbackUsers();
      setMessageableUsers(users);
      return users;
    } catch {
      const fallback = getFallbackUsers();
      setMessageableUsers(fallback);
      return fallback;
    } finally {
      setIsLoadingUsers(false);
    }
  }, [isAuthenticated]);

  const getFallbackUsers = () => [
    { id: 2, username: 'jane_smith', fullName: 'Jane Smith', profilePicture: 'https://i.pravatar.cc/150?img=2', isOnline: true, lastSeen: new Date(), isFollowing: true },
    { id: 3, username: 'mike_wilson', fullName: 'Mike Wilson', profilePicture: 'https://i.pravatar.cc/150?img=3', isOnline: false, lastSeen: new Date(Date.now() - 3600000), isFollowing: true }
  ];

  // ------------------ Tạo conversations từ users ------------------
  const createConversationsFromUsers = useCallback((users) => {
    if (!users?.length) return [];
    return users.map(user => ({
      id: user.id,
      user: {
        id: user.id,
        username: user.username,
        fullName: user.fullName || user.username,
        profilePicture: user.profilePicture || `https://i.pravatar.cc/150?u=${user.id}`,
        isOnline: user.isOnline || Math.random() > 0.5,
        lastSeen: user.lastSeen || new Date()
      },
      lastMessage: { content: 'Bắt đầu cuộc trò chuyện', messageType: 'TEXT', timestamp: new Date(), isRead: true },
      unreadCount: 0,
      canMessage: user.isFollowing
    }));
  }, []);

  // ------------------ Load Conversations ------------------
  const loadConversations = useCallback(async () => {
    if (!isAuthenticated) return;
    setIsLoadingConversations(true);
    try {
      const users = await loadMessageableUsers();
      const convList = createConversationsFromUsers(users);
      setConversations(convList);

      let selected = convList.find(c => c.id === parseInt(conversationId)) || convList[0];
      setActiveConversation(selected);
    } catch {
      setConversations(createConversationsFromUsers(getFallbackUsers()));
    } finally {
      setIsLoadingConversations(false);
    }
  }, [isAuthenticated, conversationId, loadMessageableUsers, createConversationsFromUsers]);

  useEffect(() => { loadConversations(); }, [loadConversations]);

  // ------------------ WebSocket ------------------
  useEffect(() => {
    if (isAuthenticated && user?.id) {
      chatService.connect(user.id, () => console.log('WS connected'), console.error);
    }
    return () => chatService.disconnect();
  }, [isAuthenticated, user]);

  // ------------------ Typing indicator ------------------
  useEffect(() => {
    const handleTyping = (data) => { 
      if (data.userId === activeConversation?.id) setIsTyping(data.isTyping); 
    };
    chatService.onTyping(handleTyping);
    return () => chatService.offTyping(handleTyping);
  }, [activeConversation]);

  const handleTypingIndicator = useCallback((typing) => {
    if (activeConversation) chatService.sendTypingIndicator(activeConversation.id, typing);
  }, [activeConversation]);

  // ------------------ Handle Send Message ------------------
  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!newMessage.trim() || !activeConversation) return;
    setIsSending(true);
    try {
      await sendTextMessage(newMessage); // Gửi REST + WS đã handle
      setNewMessage('');
      handleTypingIndicator(false);
    } finally { setIsSending(false); }
  };

  // ------------------ Chọn conversation ------------------
  const handleConversationSelect = (conversation) => {
    setActiveConversation(conversation);
    navigate(`/messages/${conversation.id}`);
    if (conversation.unreadCount > 0) markAsRead(); // dùng hook để update local state
  };

  // ------------------ Bắt đầu chat mới ------------------
  const startNewConversation = (user) => {
    const newConv = {
      id: user.id,
      user: {
        id: user.id,
        username: user.username,
        fullName: user.fullName || user.username,
        profilePicture: user.profilePicture || `https://i.pravatar.cc/150?u=${user.id}`,
        isOnline: user.isOnline || true,
        lastSeen: new Date()
      },
      lastMessage: { content: 'Bắt đầu cuộc trò chuyện', messageType: 'TEXT', timestamp: new Date(), isRead: true },
      unreadCount: 0,
      canMessage: true
    };
    setConversations(prev => prev.find(c => c.id === user.id) ? prev : [newConv, ...prev]);
    setActiveConversation(newConv);
    setShowNewChatModal(false);
    navigate(`/messages/${user.id}`);
  };

  // ------------------ Helpers hiển thị ------------------
  const formatTime = (timestamp) => {
    if (!timestamp) return '';
    const now = new Date(), msgTime = new Date(timestamp);
    const diffH = (now - msgTime) / (1000*60*60);
    if (diffH < 1) return Math.max(Math.floor(diffH*60),1) + 'm';
    if (diffH < 24) return Math.floor(diffH)+'h';
    return msgTime.toLocaleDateString();
  };

  const getMessagePreview = (message) => {
    if (!message) return 'Bắt đầu cuộc trò chuyện';
    const icons = { TEXT:'💬', IMAGE:'🖼️', VIDEO:'🎥', VOICE:'🎤', FILE:'📎', LOCATION:'📍', STICKER:'😊', SYSTEM:'⚙️' };
    const icon = icons[message.messageType] || '💬';
    switch (message.messageType) {
      case 'TEXT': return `${icon} ${message.content}`;
      case 'IMAGE': return `${icon} Đã gửi hình ảnh`;
      case 'VIDEO': return `${icon} Đã gửi video`;
      case 'VOICE': return `${icon} Tin nhắn thoại`;
      case 'FILE': return `${icon} Tệp đính kèm`;
      case 'LOCATION': return `${icon} Vị trí`;
      case 'STICKER': return `${icon} Nhãn dán`;
      default: return `${icon} ${message.content}`;
    }
  };

  const getMessageIcon = (type) => ({ TEXT:'💬', IMAGE:'🖼️', VIDEO:'🎥', VOICE:'🎤', FILE:'📎', LOCATION:'📍', STICKER:'😊', SYSTEM:'⚙️' }[type]||'💬');

  const filteredConversations = conversations.filter(c =>
    c.user.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
    c.user.fullName.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // ------------------ New Chat Modal ------------------
  const NewChatModal = () => {
    const [searchQuery, setSearchQuery] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const [isSearching, setIsSearching] = useState(false);

    const handleSearch = async (query) => {
      setSearchQuery(query);
      if (!query.trim()) return setSearchResults([]);
      setIsSearching(true);
      try {
        const res = await userService.searchMessageableUsers(query);
        setSearchResults(res.content || []);
      } catch { setSearchResults([]); } finally { setIsSearching(false); }
    };

    const displayUsers = searchQuery ? searchResults : messageableUsers.filter(u =>
      u.username.toLowerCase().includes(searchQuery.toLowerCase()) ||
      u.fullName?.toLowerCase().includes(searchQuery.toLowerCase())
    );

    return (
      <div className={styles.modalOverlay} onClick={() => setShowNewChatModal(false)}>
        <div className={styles.modalContent} onClick={e=>e.stopPropagation()}>
          <div className={styles.modalHeader}>
            <h3>Tin nhắn mới</h3>
            <button className={styles.closeBtn} onClick={()=>setShowNewChatModal(false)}>✕</button>
          </div>
          <div className={styles.searchContainer}>
            <input type="text" placeholder="Tìm kiếm người dùng..." value={searchQuery} onChange={e=>handleSearch(e.target.value)} className={styles.searchInput}/>
          </div>
          <div className={styles.usersList}>
            {isSearching ? <div className={styles.loading}>Đang tìm kiếm...</div> :
            displayUsers.length ? displayUsers.map(u => (
              <div key={u.id} className={styles.userItem} onClick={()=>startNewConversation(u)}>
                <div className={styles.userAvatar}><img src={u.profilePicture} alt={u.username} onError={e=>e.target.src=`https://ui-avatars.com/api/?name=${u.username}&background=random`} /></div>
                <div className={styles.userInfo}>
                  <span className={styles.username}>{u.username}</span>
                  <span className={styles.fullName}>{u.fullName}</span>
                </div>
                {!u.isFollowing && <span className={styles.followHint}>Theo dõi để nhắn tin</span>}
              </div>
            )) : <div className={styles.noUsers}>{searchQuery ? 'Không tìm thấy người dùng' : 'Chưa có người để nhắn tin'}</div>}
          </div>
        </div>
      </div>
    );
  };

  // ------------------ UI ------------------
  if (!isAuthenticated) return (
    <div className={styles.container}>
      <div className={styles.loginPrompt}>
        <h2>Messages</h2>
        <p>Please log in to view your messages</p>
        <button onClick={()=>navigate('/login')} className={styles.loginBtn}>Log In</button>
      </div>
    </div>
  );

  return (
    <div className={styles.container}>
      {/* Header */}
      <header className={styles.header}>
        <div className={styles.headerContent}>
          <button className={styles.backBtn} onClick={()=>navigate('/')}><span>←</span></button>
          <h1 className={styles.headerTitle}>Messages</h1>
          <button className={styles.newChatBtn} onClick={()=>setShowNewChatModal(true)}><span>✏️</span></button>
        </div>
      </header>

      <div className={styles.content}>
        {/* Sidebar */}
        <div className={styles.sidebar}>
          <div className={styles.sidebarHeader}>
            <h2 className={styles.sidebarTitle}>{user?.username}</h2>
            <button className={styles.newMessageBtn} onClick={()=>setShowNewChatModal(true)}><span>✉️</span></button>
          </div>
          <div className={styles.searchContainer}>
            <input type="text" placeholder="Tìm kiếm tin nhắn..." value={searchTerm} onChange={e=>setSearchTerm(e.target.value)} className={styles.searchInput}/>
          </div>
          <div className={styles.conversationsList}>
            {isLoadingConversations ? <div className={styles.loadingConversations}><div className={styles.loadingSpinner}></div><p>Đang tải danh sách trò chuyện...</p></div> :
            filteredConversations.length ? filteredConversations.map(conv => (
              <div key={conv.id} className={`${styles.conversationItem} ${activeConversation?.id===conv.id?styles.active:''}`} onClick={()=>handleConversationSelect(conv)}>
                <div className={styles.conversationAvatar}>
                  <img src={conv.user.profilePicture} alt={conv.user.username} onError={e=>e.target.src=`https://ui-avatars.com/api/?name=${conv.user.username}&background=random`}/>
                  {conv.user.isOnline && <span className={styles.onlineIndicator}></span>}
                </div>
                <div className={styles.conversationInfo}>
                  <div className={styles.conversationHeader}>
                    <span className={styles.username}>{conv.user.username}</span>
                    <span className={styles.timestamp}>{formatTime(conv.lastMessage?.timestamp)}</span>
                  </div>
                  <div className={styles.conversationPreview}>
                    <span className={`${styles.messagePreview} ${conv.unreadCount>0?styles.unread:''}`}>{getMessagePreview(conv.lastMessage)}</span>
                    {conv.unreadCount>0 && <span className={styles.unreadBadge}>{conv.unreadCount}</span>}
                  </div>
                </div>
              </div>
            )) : <div className={styles.noConversations}><span className={styles.noConversationsIcon}>💬</span><p>Chưa có cuộc trò chuyện nào</p><button className={styles.startChatBtn} onClick={()=>setShowNewChatModal(true)}>Bắt đầu cuộc trò chuyện mới</button></div>}
          </div>
        </div>

        {/* Chat Area */}
        <div className={styles.chatArea}>
          {activeConversation ? (
            <>
              {/* Chat Header */}
              <div className={styles.chatHeader}>
                <div className={styles.chatUserInfo}>
                  <div className={styles.chatAvatar}>
                    <img src={activeConversation.user.profilePicture} alt={activeConversation.user.username} onError={e=>e.target.src=`https://ui-avatars.com/api/?name=${activeConversation.user.username}&background=random`}/>
                    {activeConversation.user.isOnline && <span className={styles.onlineIndicator}></span>}
                  </div>
                  <div className={styles.chatUserDetails}>
                    <span className={styles.username}>{activeConversation.user.username}</span>
                    <span className={styles.userStatus}>{activeConversation.user.isOnline?'Online':`Hoạt động ${formatTime(activeConversation.user.lastSeen)}`}</span>
                  </div>
                </div>
              </div>

              {/* Messages */}
              <div className={styles.messagesList}>
                {isLoading && !messages.length ? <div className={styles.loading}><div className={styles.loadingSpinner}></div><p>Đang tải tin nhắn...</p></div> :
                error ? <div className={styles.error}>Lỗi: {error}</div> :
                messages.length ? (
                  <>
                    {hasMore && <button onClick={loadMore} className={styles.loadMoreBtn}>Tải tin nhắn cũ hơn</button>}
                    {messages.map(msg=>(
                      <div key={msg.id} className={`${styles.message} ${msg.senderId===user.id?styles.sent:styles.received}`}>
                        <div className={styles.messageBubble}>
                          {msg.messageType!=='TEXT' && <span className={styles.messageIcon}>{getMessageIcon(msg.messageType)}</span>}
                          <p className={styles.messageText}>{msg.content}</p>
                          <span className={styles.messageTime}>
                            {formatTime(msg.createdAt)}
                            {msg.isRead && msg.senderId===user.id && <span className={styles.readIndicator}> ✓✓</span>}
                          </span>
                        </div>
                      </div>
                    ))}
                    {isTyping && <div className={`${styles.message} ${styles.received}`}><div className={styles.typingIndicator}><span></span><span></span><span></span></div></div>}
                    <div ref={messagesEndRef}/>
                  </>
                ) : <div className={styles.emptyChat}><span className={styles.emptyChatIcon}>💬</span><p>Chưa có tin nhắn nào</p><p>Bắt đầu trò chuyện bằng cách gửi tin nhắn!</p></div>}
              </div>

              {/* Message Input */}
              <form onSubmit={handleSendMessage} className={styles.messageInputContainer}>
                <div className={styles.inputWrapper}>
                  <input type="text" value={newMessage} onChange={e=>{setNewMessage(e.target.value); handleTypingIndicator(true);}} onBlur={()=>handleTypingIndicator(false)} placeholder="Nhập tin nhắn..." className={styles.messageInput} disabled={isSending}/>
                  <button type="submit" className={styles.sendBtn} disabled={!newMessage.trim()||isSending}>{isSending?'⏳':'➤'}</button>
                </div>
              </form>
            </>
          ) : <div className={styles.noConversationSelected}><div className={styles.emptyState}><span className={styles.emptyStateIcon}>💬</span><h3>Tin nhắn của bạn</h3><p>Chọn một cuộc trò chuyện để bắt đầu nhắn tin</p></div></div>}
        </div>
      </div>

      {showNewChatModal && <NewChatModal />}
    </div>
  );
};

export default Messages;

