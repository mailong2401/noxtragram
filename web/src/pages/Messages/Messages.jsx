// components/Messages/Messages.js
import React, { useState, useEffect, useRef } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { Link, useNavigate, useParams } from 'react-router-dom';
import userService from '../../services/userService';
import styles from './Messages.module.css';

const Messages = () => {
    const { user, isAuthenticated } = useAuth();
    const [conversations, setConversations] = useState([]);
    const [activeConversation, setActiveConversation] = useState(null);
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [searchTerm, setSearchTerm] = useState('');
    const [isLoading, setIsLoading] = useState(true);
    const [isSending, setIsSending] = useState(false);
    const messagesEndRef = useRef(null);
    const navigate = useNavigate();
    const { conversationId } = useParams();

    useEffect(() => {
        if (!isAuthenticated) return;

        const mockConversations = [
            {
                id: 1,
                user: {
                    id: 2,
                    username: 'jane_smith',
                    fullName: 'Jane Smith',
                    profilePicture: 'https://i.pravatar.cc/150?img=2',
                    isOnline: true,
                    lastSeen: new Date()
                },
                lastMessage: {
                    text: 'Hey, how are you doing?',
                    timestamp: new Date(Date.now() - 300000),
                    isRead: true,
                    isSender: false
                },
                unreadCount: 0
            },
            {
                id: 2,
                user: {
                    id: 3,
                    username: 'mike_wilson',
                    fullName: 'Mike Wilson',
                    profilePicture: 'https://i.pravatar.cc/150?img=3',
                    isOnline: false,
                    lastSeen: new Date(Date.now() - 3600000)
                },
                lastMessage: {
                    text: 'Check out this photo! 📸',
                    timestamp: new Date(Date.now() - 1800000),
                    isRead: false,
                    isSender: false
                },
                unreadCount: 2
            },
            {
                id: 3,
                user: {
                    id: 4,
                    username: 'sarah_j',
                    fullName: 'Sarah Johnson',
                    profilePicture: 'https://i.pravatar.cc/150?img=4',
                    isOnline: true,
                    lastSeen: new Date()
                },
                lastMessage: {
                    text: 'See you tomorrow!',
                    timestamp: new Date(Date.now() - 86400000),
                    isRead: true,
                    isSender: true
                },
                unreadCount: 0
            },
            {
                id: 4,
                user: {
                    id: 5,
                    username: 'alex_turner',
                    fullName: 'Alex Turner',
                    profilePicture: 'https://i.pravatar.cc/150?img=5',
                    isOnline: true,
                    lastSeen: new Date()
                },
                lastMessage: {
                    text: 'Are we still meeting today?',
                    timestamp: new Date(Date.now() - 7200000),
                    isRead: false,
                    isSender: false
                },
                unreadCount: 1
            }
        ];

        setConversations(mockConversations);
        setIsLoading(false);

        // Auto-select conversation từ URL hoặc chọn cái đầu tiên
        let selectedConversation = null;
        if (conversationId) {
            selectedConversation = mockConversations.find(c => c.id === parseInt(conversationId));
        }
        
        if (!selectedConversation && mockConversations.length > 0) {
            selectedConversation = mockConversations[0];
        }
        
        setActiveConversation(selectedConversation);
        if (selectedConversation) {
            loadMessages(selectedConversation.id);
        }
    }, [isAuthenticated, conversationId]);

    const loadMessages = (conversationId) => {
        const mockMessages = {
            1: [
                {
                    id: 1,
                    text: 'Hi there! 👋',
                    timestamp: new Date(Date.now() - 3600000),
                    isSender: false,
                    isRead: true
                },
                {
                    id: 2,
                    text: 'Hello! How are you?',
                    timestamp: new Date(Date.now() - 3500000),
                    isSender: true,
                    isRead: true
                },
                {
                    id: 3,
                    text: "I'm good, thanks! How about you?",
                    timestamp: new Date(Date.now() - 3400000),
                    isSender: false,
                    isRead: true
                },
                {
                    id: 4,
                    text: "I'm doing great! Just working on some projects.",
                    timestamp: new Date(Date.now() - 3300000),
                    isSender: true,
                    isRead: true
                }
            ],
            2: [
                {
                    id: 1,
                    text: 'Nice photo! 📷',
                    timestamp: new Date(Date.now() - 7200000),
                    isSender: true,
                    isRead: true
                },
                {
                    id: 2,
                    text: 'Thanks! I took it yesterday.',
                    timestamp: new Date(Date.now() - 7100000),
                    isSender: false,
                    isRead: true
                }
            ],
            3: [
                {
                    id: 1,
                    text: 'Meeting tomorrow at 3 PM?',
                    timestamp: new Date(Date.now() - 172800000),
                    isSender: false,
                    isRead: true
                },
                {
                    id: 2,
                    text: 'Yes, that works for me!',
                    timestamp: new Date(Date.now() - 172700000),
                    isSender: true,
                    isRead: true
                }
            ],
            4: [
                {
                    id: 1,
                    text: 'Hey, are we still meeting today?',
                    timestamp: new Date(Date.now() - 7200000),
                    isSender: false,
                    isRead: false
                },
                {
                    id: 2,
                    text: 'Yes, 2 PM at the usual place!',
                    timestamp: new Date(Date.now() - 3600000),
                    isSender: true,
                    isRead: true
                }
            ]
        };

        setMessages(mockMessages[conversationId] || []);
    };

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    const handleSendMessage = async (e) => {
        e.preventDefault();
        if (!newMessage.trim() || !activeConversation) return;

        setIsSending(true);

        const newMsg = {
            id: messages.length + 1,
            text: newMessage,
            timestamp: new Date(),
            isSender: true,
            isRead: false
        };

        setMessages(prev => [...prev, newMsg]);
        setNewMessage('');

        // Simulate reply
        setTimeout(() => {
            const replyMsg = {
                id: messages.length + 2,
                text: getRandomReply(),
                timestamp: new Date(),
                isSender: false,
                isRead: false
            };
            setMessages(prev => [...prev, replyMsg]);
        }, 1000 + Math.random() * 2000);

        setIsSending(false);
    };

    const getRandomReply = () => {
        const replies = [
            "That's interesting!",
            "I see what you mean",
            "Tell me more about that",
            "I agree with you",
            "That's amazing!",
            "Thanks for sharing",
            "I'll think about it",
            "Sounds good to me"
        ];
        return replies[Math.floor(Math.random() * replies.length)];
    };

    const handleConversationSelect = (conversation) => {
        setActiveConversation(conversation);
        loadMessages(conversation.id);
        navigate(`/messages/${conversation.id}`);
    };

    const filteredConversations = conversations.filter(conv =>
        conv.user.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
        conv.user.fullName.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const formatTime = (timestamp) => {
        const now = new Date();
        const messageTime = new Date(timestamp);
        const diffInHours = (now - messageTime) / (1000 * 60 * 60);

        if (diffInHours < 1) {
            const diffInMinutes = Math.floor(diffInHours * 60);
            return diffInMinutes === 0 ? 'now' : `${diffInMinutes}m`;
        } else if (diffInHours < 24) {
            return `${Math.floor(diffInHours)}h`;
        } else {
            return messageTime.toLocaleDateString();
        }
    };

    if (!isAuthenticated) {
        return (
            <div className={styles.container}>
                <div className={styles.loginPrompt}>
                    <h2>Messages</h2>
                    <p>Please log in to view your messages</p>
                    <button onClick={() => navigate('/login')} className={styles.loginBtn}>
                        Log In
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className={styles.container}>
            {/* Header */}
            <header className={styles.header}>
                <div className={styles.headerContent}>
                    <button className={styles.backBtn} onClick={() => navigate('/')}>
                        <span>←</span>
                    </button>
                    <h1 className={styles.headerTitle}>Messages</h1>
                    <button className={styles.newChatBtn}>
                        <span>✏️</span>
                    </button>
                </div>
            </header>

            <div className={styles.content}>
                {/* Conversations List - LUÔN HIỂN THỊ */}
                <div className={styles.sidebar}>
                    <div className={styles.sidebarHeader}>
                        <h2 className={styles.sidebarTitle}>{user?.username}</h2>
                        <button className={styles.newMessageBtn}>
                            <span>✉️</span>
                        </button>
                    </div>

                    <div className={styles.searchContainer}>
                        <input
                            type="text"
                            placeholder="Search messages..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className={styles.searchInput}
                        />
                    </div>

                    <div className={styles.conversationsList}>
                        {filteredConversations.map(conversation => (
                            <div
                                key={conversation.id}
                                className={`${styles.conversationItem} ${activeConversation?.id === conversation.id ? styles.active : ''}`}
                                onClick={() => handleConversationSelect(conversation)}
                            >
                                <div className={styles.conversationAvatar}>
                                    <img src={conversation.user.profilePicture} alt={conversation.user.username} />
                                    {conversation.user.isOnline && <span className={styles.onlineIndicator}></span>}
                                </div>
                                <div className={styles.conversationInfo}>
                                    <div className={styles.conversationHeader}>
                                        <span className={styles.username}>{conversation.user.username}</span>
                                        <span className={styles.timestamp}>{formatTime(conversation.lastMessage.timestamp)}</span>
                                    </div>
                                    <div className={styles.conversationPreview}>
                                        <span className={`${styles.messagePreview} ${!conversation.lastMessage.isRead && !conversation.lastMessage.isSender ? styles.unread : ''}`}>
                                            {conversation.lastMessage.isSender ? 'You: ' : ''}
                                            {conversation.lastMessage.text}
                                        </span>
                                        {conversation.unreadCount > 0 && (
                                            <span className={styles.unreadBadge}>{conversation.unreadCount}</span>
                                        )}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Chat Area - LUÔN HIỂN THỊ */}
                <div className={styles.chatArea}>
                    {activeConversation ? (
                        <>
                            {/* Chat Header */}
                            <div className={styles.chatHeader}>
                                <div className={styles.chatUserInfo}>
                                    <div className={styles.chatAvatar}>
                                        <img src={activeConversation.user.profilePicture} alt={activeConversation.user.username} />
                                        {activeConversation.user.isOnline && <span className={styles.onlineIndicator}></span>}
                                    </div>
                                    <div className={styles.chatUserDetails}>
                                        <span className={styles.username}>{activeConversation.user.username}</span>
                                        <span className={styles.userStatus}>
                                            {activeConversation.user.isOnline ? 'Online' : `Last seen ${formatTime(activeConversation.user.lastSeen)}`}
                                        </span>
                                    </div>
                                </div>
                                <div className={styles.chatActions}>
                                    <button className={styles.actionBtn}>
                                        <span>📞</span>
                                    </button>
                                    <button className={styles.actionBtn}>
                                        <span>ⓘ</span>
                                    </button>
                                </div>
                            </div>

                            {/* Messages List */}
                            <div className={styles.messagesList}>
                                {messages.length > 0 ? (
                                    <>
                                        {messages.map(message => (
                                            <div
                                                key={message.id}
                                                className={`${styles.message} ${message.isSender ? styles.sent : styles.received}`}
                                            >
                                                <div className={styles.messageBubble}>
                                                    <p className={styles.messageText}>{message.text}</p>
                                                    <span className={styles.messageTime}>{formatTime(message.timestamp)}</span>
                                                </div>
                                            </div>
                                        ))}
                                        <div ref={messagesEndRef} />
                                    </>
                                ) : (
                                    <div className={styles.emptyChat}>
                                        <span className={styles.emptyChatIcon}>💬</span>
                                        <p>No messages yet</p>
                                        <p>Start a conversation by sending a message!</p>
                                    </div>
                                )}
                            </div>

                            {/* Message Input */}
                            <form onSubmit={handleSendMessage} className={styles.messageInputContainer}>
                                <div className={styles.inputWrapper}>
                                    <button type="button" className={styles.attachmentBtn}>
                                        <span>📎</span>
                                    </button>
                                    <input
                                        type="text"
                                        value={newMessage}
                                        onChange={(e) => setNewMessage(e.target.value)}
                                        placeholder="Message..."
                                        className={styles.messageInput}
                                        disabled={isSending}
                                    />
                                    <button
                                        type="submit"
                                        className={styles.sendBtn}
                                        disabled={!newMessage.trim() || isSending}
                                    >
                                        {isSending ? '⏳' : '➤'}
                                    </button>
                                </div>
                            </form>
                        </>
                    ) : (
                        <div className={styles.noConversationSelected}>
                            <div className={styles.emptyState}>
                                <span className={styles.emptyStateIcon}>💬</span>
                                <h3>Your Messages</h3>
                                <p>Select a conversation to start messaging</p>
                                <p>Or start a new conversation with someone!</p>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Messages;
