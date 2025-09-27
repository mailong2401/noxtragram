// Home.js
import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { Link, useNavigate } from 'react-router-dom';

import './Home.css';

const Home = () => {
    const { user, isAuthenticated, logout } = useAuth();
    const [posts, setPosts] = useState([]);
    const [stories, setStories] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const navigate = useNavigate();


    // Mock data for posts (trong thực tế sẽ fetch từ API)
    useEffect(() => {
        const mockPosts = [
            {
                id: 1,
                username: 'john_doe',
                avatar: 'https://i.pravatar.cc/150?img=1',
                image: 'https://picsum.photos/600/600?random=1',
                caption: 'Beautiful day! 🌞 #sunshine',
                likes: 243,
                comments: 15,
                timestamp: '2 hours ago'
            },
            {
                id: 2,
                username: 'jane_smith',
                avatar: 'https://i.pravatar.cc/150?img=2',
                image: 'https://picsum.photos/600/600?random=2',
                caption: 'Coffee time ☕ #morningvibes',
                likes: 187,
                comments: 8,
                timestamp: '4 hours ago'
            },
            {
                id: 3,
                username: 'mike_wilson',
                avatar: 'https://i.pravatar.cc/150?img=3',
                image: 'https://picsum.photos/600/600?random=3',
                caption: 'Weekend adventures! 🏞️',
                likes: 321,
                comments: 24,
                timestamp: '6 hours ago'
            }
        ];

        const mockStories = [
            { id: 1, username: 'john_doe', avatar: 'https://i.pravatar.cc/150?img=1', hasNew: true },
            { id: 2, username: 'jane_smith', avatar: 'https://i.pravatar.cc/150?img=2', hasNew: true },
            { id: 3, username: 'mike_wilson', avatar: 'https://i.pravatar.cc/150?img=3', hasNew: false },
            { id: 4, username: 'sarah_j', avatar: 'https://i.pravatar.cc/150?img=4', hasNew: true },
            { id: 5, username: 'tom_cruise', avatar: 'https://i.pravatar.cc/150?img=5', hasNew: false }
        ];

        setPosts(mockPosts);
        setStories(mockStories);
        setIsLoading(false);
    }, []);

    const handleLike = (postId) => {
        setPosts(posts.map(post => 
            post.id === postId ? { ...post, likes: post.likes + 1 } : post
        ));
    };

    const handleLogout = () => {
        logout();
    };

    if (!isAuthenticated) {
        return (
            <div className="home-container">
                <div className="login-prompt">
                    <h2>Welcome to Instagram Clone</h2>
                    <p>Please log in to see the content</p>
                    <Link to="/login" className="login-btn">Log In</Link>
                </div>
            </div>
        );
    }

    if (isLoading) {
        return (
            <div className="loading-container">
                <div className="loading-spinner"></div>
                <p>Loading posts...</p>
            </div>
        );
    }

    return (
        <div className="home-container">
                        {/* Stories Section */}
            <div className="stories-container">
                <div className="stories">
                    {stories.map(story => (
                        <div key={story.id} className="story">
                            <div className={`story-avatar ${story.hasNew ? 'has-new' : ''}`}>
                                <img src={story.avatar} alt={story.username} />
                            </div>
                            <span className="story-username">{story.username}</span>
                        </div>
                    ))}
                </div>
            </div>

            {/* Posts Section */}
            <div className="posts-container">
                {posts.map(post => (
                    <div key={post.id} className="post">
                        {/* Post Header */}
                        <div className="post-header">
                            <div className="post-user">
                                <img src={post.avatar} alt={post.username} className="post-avatar" />
                                <span className="post-username">{post.username}</span>
                            </div>
                            <button className="post-options">
                                <i className="fas fa-ellipsis-h"></i>
                            </button>
                        </div>

                        {/* Post Image */}
                        <div className="post-image">
                            <img src={post.image} alt={`Post by ${post.username}`} />
                        </div>

                        {/* Post Actions */}
                        <div className="post-actions">
                            <button 
                                className="action-btn"
                                onClick={() => handleLike(post.id)}
                            >
                                <i className="far fa-heart"></i>
                            </button>
                            <button className="action-btn">
                                <i className="far fa-comment"></i>
                            </button>
                            <button className="action-btn">
                                <i className="far fa-paper-plane"></i>
                            </button>
                            <button className="action-btn save-btn">
                                <i className="far fa-bookmark"></i>
                            </button>
                        </div>

                        {/* Post Details */}
                        <div className="post-details">
                            <div className="post-likes">{post.likes} likes</div>
                            <div className="post-caption">
                                <strong>{post.username}</strong> {post.caption}
                            </div>
                            <div className="post-comments">
                                View all {post.comments} comments
                            </div>
                            <div className="post-timestamp">{post.timestamp}</div>
                        </div>

                        {/* Add Comment */}
                        <div className="add-comment">
                            <input 
                                type="text" 
                                placeholder="Add a comment..." 
                                className="comment-input"
                            />
                            <button className="post-comment-btn">Post</button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Home;
