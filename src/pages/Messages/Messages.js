// components/Messages/Messages.js
import React, { useState, useEffect, useRef } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { Link, useNavigate, useParams } from 'react-router-dom';
import userService from '../../services/userService';
import './Messages.css';

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

    // Mock data - trong thực tế sẽ fetch từ API
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
                    timestamp: new Date(Date.now() - 300000), // 5 phút trước
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
                    lastSeen: new Date(Date.now() - 3600000) // 1 giờ trước
                },
                lastMessage: {
                    text: 'Check out this photo! 📸',
                    timestamp: new Date(Date.now() - 1800000), // 30 phút trước
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
                    timestamp: new Date(Date.now() - 86400000), // 1 ngày trước
                    isRead: true,
                    isSender: true
                },
                unreadCount: 0
            }
        ];

        setConversations(mockConversations);
        setIsLoading(false);

        // Auto-select first conversation or from URL parameter
        if (conversationId) {
            const conv = mockConversations.find(c => c.id === parseInt(conversationId));
            if (conv) {
                setActiveConversation(conv);
                loadMessages(conv.id);
            }
        } else if (mockConversations.length > 0) {
            setActiveConversation(mockConversations[0]);
            loadMessages(mockConversations[0].id);
        }
    }, [isAuthenticated, conversationId]);

    const loadMessages = (conversationId) => {
        // Mock messages data
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
                },
                {
                    id: 5,
                    text: 'That sounds interesting!',
                    timestamp: new Date(Date.now() - 3200000),
                    isSender: false,
                    isRead: true
                },
                {
                    id: 6,
                    text: 'Hey, how are you doing?',
                    timestamp: new Date(Date.now() - 300000),
                    isSender: false,
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
                },
                {
                    id: 3,
                    text: 'Check out this photo! 📸',
                    timestamp: new Date(Date.now() - 1800000),
                    isSender: false,
                    isRead: false
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
                },
                {
                    id: 3,
                    text: 'Great! See you then.',
                    timestamp: new Date(Date.now() - 172600000),
                    isSender: false,
                    isRead: true
                },
                {
                    id: 4,
                    text: 'See you tomorrow!',
                    timestamp: new Date(Date.now() - 86400000),
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

        // Mock sending message
        const newMsg = {
            id: messages.length + 1,
            text: newMessage,
            timestamp: new Date(),
            isSender: true,
            isRead: false
        };

        setMessages(prev => [...prev, newMsg]);
        setNewMessage('');

        // Simulate reply after 1-3 seconds
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
            <div className="messages-container">
                <div className="login-prompt">
                    <h2>Messages</h2>
                    <p>Please log in to view your messages</p>
                    <button onClick={() => navigate('/login')} className="login-btn">
                        Log In
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="messages-container">
            {/* Header */}
            <header className="messages-header">
                <div className="header-content">
                    <button className="back-btn" onClick={() => navigate('/')}>
                        <span className="icon">←</span>
                    </button>
                    <h1 className="header-title">{activeConversation ? activeConversation.user.username : 'Messages'}</h1>
                    <button className="new-chat-btn">
                        <span className="icon">✏️</span>
                    </button>
                </div>
            </header>

            <div className="messages-content">
                {/* Conversations List */}
                <div className={`conversations-sidebar ${activeConversation ? 'collapsed' : ''}`}>
                    <div className="conversations-header">
                        <h2 className="sidebar-title">{user?.username}</h2>
                        <button className="new-message-btn">
                            <span className="icon">✉️</span>
                        </button>
                    </div>

                    <div className="search-container">
                        <input
                            type="text"
                            placeholder="Search messages..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="search-input"
                        />
                    </div>

                    <div className="conversations-list">
                        {filteredConversations.map(conversation => (
                            <div
                                key={conversation.id}
                                className={`conversation-item ${activeConversation?.id === conversation.id ? 'active' : ''}`}
                                onClick={() => handleConversationSelect(conversation)}
                            >
                                <div className="conversation-avatar">
                                    <img src={conversation.user.profilePicture} alt={conversation.user.username} />
                                    {conversation.user.isOnline && <span className="online-indicator"></span>}
                                </div>
                                <div className="conversation-info">
                                    <div className="conversation-header">
                                        <span className="username">{conversation.user.username}</span>
                                        <span className="timestamp">{formatTime(conversation.lastMessage.timestamp)}</span>
                                    </div>
                                    <div className="conversation-preview">
                                        <span className={`message-preview ${!conversation.lastMessage.isRead && !conversation.lastMessage.isSender ? 'unread' : ''}`}>
                                            {conversation.lastMessage.isSender ? 'You: ' : ''}
                                            {conversation.lastMessage.text}
                                        </span>
                                        {conversation.unreadCount > 0 && (
                                            <span className="unread-badge">{conversation.unreadCount}</span>
                                        )}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Chat Area */}
                {activeConversation ? (
                    <div className="chat-area">
                        {/* Chat Header */}
                        <div className="chat-header">
                            <div className="chat-user-info">
                                <div className="chat-avatar">
                                    <img src={activeConversation.user.profilePicture} alt={activeConversation.user.username} />
                                    {activeConversation.user.isOnline && <span className="online-indicator"></span>}
                                </div>
                                <div className="chat-user-details">
                                    <span className="username">{activeConversation.user.username}</span>
                                    <span className="user-status">
                                        {activeConversation.user.isOnline ? 'Online' : `Last seen ${formatTime(activeConversation.user.lastSeen)}`}
                                    </span>
                                </div>
                            </div>
                            <div className="chat-actions">
                                <button className="action-btn">
                    <span className="icon">📞</span>
                                </button>
                                <button className="action-btn">
                                    <span className="icon">ⓘ</span>
                                </button>
                            </div>
                        </div>

                        {/* Messages List */}
                        <div className="messages-list">
                            {messages.map(message => (
                                <div
                                    key={message.id}
                                    className={`message ${message.isSender ? 'sent' : 'received'}`}
                                >
                                    <div className="message-bubble">
                                        <p className="message-text">{message.text}</p>
                                        <span className="message-time">{formatTime(message.timestamp)}</span>
                                    </div>
                                </div>
                            ))}
                            <div ref={messagesEndRef} />
                        </div>

                        {/* Message Input */}
                        <form onSubmit={handleSendMessage} className="message-input-container">
                            <div className="input-wrapper">
                                <button type="button" className="attachment-btn">
                                    <span className="icon">📎</span>
                                </button>
                                <input
                                    type="text"
                                    value={newMessage}
                                    onChange={(e) => setNewMessage(e.target.value)}
                                    placeholder="Message..."
                                    className="message-input"
                                    disabled={isSending}
                                />
                                <button
                                    type="submit"
                                    className="send-btn"
                                    disabled={!newMessage.trim() || isSending}
                                >
                                    {isSending ? '⏳' : '➤'}
                                </button>
                            </div>
                        </form>
                    </div>
                ) : (
                    <div className="no-conversation-selected">
                        <div className="empty-state">
                            <span className="icon">💬</span>
                            <h3>Your Messages</h3>
                            <p>Send private messages to a friend or group.</p>
                            <button className="start-chat-btn">Send Message</button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Messages;
