import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import './Profile.css';
import { useNavigate } from 'react-router-dom';
import userService from '../../services/userService.js';
import postService from '../../services/postService.js';

const Profile = () => {
    const { user, isAuthenticated, updateUser } = useAuth();
    const [profileData, setProfileData] = useState(null);
    const [posts, setPosts] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isProfileLoading, setIsProfileLoading] = useState(true);
    const [activeTab, setActiveTab] = useState('posts');
    const [followers, setFollowers] = useState([]);
    const [following, setFollowing] = useState([]);
    const navigate = useNavigate();

    // Hàm thêm token vào URL
    const getImageUrlWithToken = (imageUrl) => {
        if (!imageUrl) return null;
        
        // Nếu đã là URL external thì giữ nguyên
        if (imageUrl.includes('pravatar.cc') || imageUrl.includes('picsum.photos') || imageUrl.startsWith('http')) {
            return imageUrl;
        }
        
        // Thêm base URL nếu là đường dẫn tương đối
        let fullUrl = imageUrl;
        if (!imageUrl.startsWith('http')) {
            fullUrl = `http://localhost:8080${imageUrl.startsWith('/') ? '' : '/'}${imageUrl}`;
        }
        
        // Thêm token vào query parameter
        const token = localStorage.getItem('token');
        const separator = fullUrl.includes('?') ? '&' : '?';
        
        return token ? `${fullUrl}${separator}token=${token}` : fullUrl;
    };

    // Fetch profile data
    useEffect(() => {
        if (isAuthenticated && user) {
            fetchProfileData();
        } else {
            setIsProfileLoading(false);
            setIsLoading(false);
        }
    }, [isAuthenticated, user]);

    const fetchProfileData = async () => {
        try {
            setIsProfileLoading(true);
            
            // Lấy thông tin user hiện tại từ backend
            const userData = await userService.getCurrentUser();
            setProfileData(userData);
            
            // Fetch additional data
            await Promise.all([
                fetchUserStats(),
                fetchPosts()
            ]);
            
        } catch (error) {
            console.error('Error fetching profile data:', error);
        } finally {
            setIsProfileLoading(false);
            setIsLoading(false);
        }
    };

    const fetchUserStats = async () => {
        try {
            if (!user?.id) return;

            const [followersList, followingList] = await Promise.all([
                userService.getFollowers(user.id),
                userService.getFollowing(user.id)
            ]);

            setFollowers(followersList);
            setFollowing(followingList);

        } catch (error) {
            console.error('Error fetching user stats:', error);
        }
    };

    const fetchPosts = async () => {
        try {
            if (!user?.id) return;
            
            const userPosts = await postService.getUserPosts(user.id);
            setPosts(userPosts.content || userPosts || []);
        } catch (error) {
            console.error('Error fetching posts:', error);
            // Fallback to empty array if error
            setPosts([]);
        }
    };

    const handleFollow = async (targetUserId) => {
        try {
            await userService.followUser(user.id, targetUserId);
            // Refresh stats after follow
            fetchUserStats();
        } catch (error) {
            console.error('Error following user:', error);
        }
    };

    const handleUnfollow = async (targetUserId) => {
        try {
            await userService.unfollowUser(user.id, targetUserId);
            // Refresh stats after unfollow
            fetchUserStats();
        } catch (error) {
            console.error('Error unfollowing user:', error);
        }
    };

    const handleEditProfile = () => {
        navigate('/edit-profile');
    };

    const handleUploadProfilePicture = async (file) => {
        try {
            const updatedUser = await userService.uploadProfilePicture(user.id, file);
            setProfileData(updatedUser);
            updateUser(updatedUser);
        } catch (error) {
            console.error('Error uploading profile picture:', error);
        }
    };

    if (!isAuthenticated) {
        return (
            <div className="profile-container">
                <div className="login-prompt">
                    <h2>Profile</h2>
                    <p>Please log in to view your profile</p>
                    <button onClick={() => navigate('/login')} className="login-btn">
                        Log In
                    </button>
                </div>
            </div>
        );
    }

    if (isProfileLoading) {
        return (
            <div className="profile-container">
                <div className="profile-loading">
                    <div className="loading-spinner"></div>
                    <p>Loading profile...</p>
                </div>
            </div>
        );
    }

    const displayData = profileData || user;
    const postsCount = posts.length;

    return (
        <div className="profile-container">
            {/* Header */}
            <header className="profile-header">
                <div className="header-content">
                    <div className="profile-back">
                        <button className="back-btn" onClick={() => navigate('/')}>
                            <span className="icon">←</span>
                        </button>
                        <div className="profile-header-info">
                            <span className="username">{displayData.username}</span>
                            <span className="posts-count">{postsCount} posts</span>
                        </div>
                    </div>
                    <div className="header-actions">
                        <button className="action-btn">
                            <span className="icon">⚙️</span>
                        </button>
                    </div>
                </div>
            </header>

            {/* Profile Info Section */}
            <section className="profile-info">
                <div className="profile-top">
                    <div className="profile-avatar-section">
                        <div className="profile-avatar">
                            <img 
                                src={getImageUrlWithToken(displayData.profilePicture) || user?.avatar || 'https://i.pravatar.cc/150?img=1'} 
                                alt={displayData.username}
                                onError={(e) => {
                                    e.target.src = 'https://i.pravatar.cc/150?img=1';
                                }}
                            />
                            <input 
                                type="file" 
                                accept="image/*" 
                                onChange={(e) => handleUploadProfilePicture(e.target.files[0])}
                                className="avatar-upload-input"
                            />
                        </div>
                    </div>
                    
                    <div className="profile-stats">
                        <div className="stat">
                            <span className="stat-number">{postsCount}</span>
                            <span className="stat-label">Posts</span>
                        </div>
                        <div className="stat" onClick={() => setActiveTab('followers')}>
                            <span className="stat-number">{displayData.followerCount || followers.length}</span>
                            <span className="stat-label">Followers</span>
                        </div>
                        <div className="stat" onClick={() => setActiveTab('following')}>
                            <span className="stat-number">{displayData.followingCount || following.length}</span>
                            <span className="stat-label">Following</span>
                        </div>
                    </div>
                </div>

                <div className="profile-details">
                    <h1 className="profile-name">{displayData.fullName || displayData.username}</h1>
                    <p className="profile-bio">{displayData.bio || 'No bio yet'}</p>
                    {displayData.website && (
                        <a href={displayData.website} className="profile-website" target="_blank" rel="noopener noreferrer">
                            {displayData.website}
                        </a>
                    )}
                </div>

                <div className="profile-actions">
                    <button className="edit-profile-btn" onClick={handleEditProfile}>
                        Edit Profile
                    </button>
                    <button className="share-profile-btn">Share Profile</button>
                    <button className="more-options-btn">⋮</button>
                </div>
            </section>

            {/* Tab Navigation */}
            <nav className="profile-tabs">
                <button 
                    className={`tab-btn ${activeTab === 'posts' ? 'active' : ''}`}
                    onClick={() => setActiveTab('posts')}
                >
                    <span className="icon">📱</span>
                    <span>POSTS</span>
                </button>
                <button 
                    className={`tab-btn ${activeTab === 'reels' ? 'active' : ''}`}
                    onClick={() => setActiveTab('reels')}
                >
                    <span className="icon">🎬</span>
                    <span>REELS</span>
                </button>
                <button 
                    className={`tab-btn ${activeTab === 'tagged' ? 'active' : ''}`}
                    onClick={() => setActiveTab('tagged')}
                >
                    <span className="icon">🏷️</span>
                    <span>TAGGED</span>
                </button>
            </nav>

            {/* Content based on active tab */}
            {activeTab === 'posts' && (
                <PostsGrid posts={posts} isLoading={isLoading} getImageUrlWithToken={getImageUrlWithToken} />
            )}

            {activeTab === 'followers' && (
                <FollowersList 
                    followers={followers} 
                    onFollow={handleFollow}
                    onUnfollow={handleUnfollow}
                    currentUserId={user.id}
                    getImageUrlWithToken={getImageUrlWithToken}
                />
            )}

            {activeTab === 'following' && (
                <FollowingList 
                    following={following} 
                    onUnfollow={handleUnfollow}
                    currentUserId={user.id}
                    getImageUrlWithToken={getImageUrlWithToken}
                />
            )}
        </div>
    );
};

// Component cho grid posts
const PostsGrid = ({ posts, isLoading, getImageUrlWithToken }) => {
    if (isLoading) {
        return (
            <div className="profile-loading">
                <div className="loading-spinner"></div>
                <p>Loading posts...</p>
            </div>
        );
    }

    if (posts.length === 0) {
        return (
            <div className="no-posts">
                <div className="no-posts-icon">📷</div>
                <h3>No Posts Yet</h3>
                <p>When you share photos and videos, they will appear here.</p>
            </div>
        );
    }

    return (
        <div className="posts-grid">
            {posts.map(post => (
                <div key={post.id} className="post-item">
                    <img 
                        src={getImageUrlWithToken(post.imageUrl) || 'https://picsum.photos/300/300'} 
                        alt={`Post ${post.id}`}
                        onError={(e) => {
                            e.target.src = 'https://picsum.photos/300/300';
                        }}
                    />
                    {post.type === 'VIDEO' && (
                        <div className="video-indicator">
                            <span className="icon">▶️</span>
                        </div>
                    )}
                    <div className="post-overlay">
                        <div className="post-stats">
                            <span className="stat">❤️ {post.likeCount || 0}</span>
                            <span className="stat">💬 {post.commentCount || 0}</span>
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
};

// Component cho danh sách followers
const FollowersList = ({ followers, onFollow, onUnfollow, currentUserId, getImageUrlWithToken }) => {
    return (
        <div className="followers-list">
            <h3>Followers ({followers.length})</h3>
            {followers.length === 0 ? (
                <div className="no-followers">
                    <p>No followers yet</p>
                </div>
            ) : (
                followers.map(follower => (
                    <div key={follower.id} className="follower-item">
                        <img 
                            src={getImageUrlWithToken(follower.profilePicture) || follower.avatar || 'https://i.pravatar.cc/150'} 
                            alt={follower.username}
                            onError={(e) => {
                                e.target.src = 'https://i.pravatar.cc/150';
                            }}
                        />
                        <div className="follower-info">
                            <span className="username">{follower.username}</span>
                            <span className="name">{follower.fullName || follower.username}</span>
                        </div>
                        {follower.id !== currentUserId && (
                            <button 
                                className="follow-btn"
                                onClick={() => follower.isFollowing ? onUnfollow(follower.id) : onFollow(follower.id)}
                            >
                                {follower.isFollowing ? 'Unfollow' : 'Follow'}
                            </button>
                        )}
                    </div>
                ))
            )}
        </div>
    );
};

// Component cho danh sách following
const FollowingList = ({ following, onUnfollow, currentUserId, getImageUrlWithToken }) => {
    return (
        <div className="following-list">
            <h3>Following ({following.length})</h3>
            {following.length === 0 ? (
                <div className="no-following">
                    <p>Not following anyone yet</p>
                </div>
            ) : (
                following.map(followed => (
                    <div key={followed.id} className="following-item">
                        <img 
                            src={getImageUrlWithToken(followed.profilePicture) || followed.avatar || 'https://i.pravatar.cc/150'} 
                            alt={followed.username}
                            onError={(e) => {
                                e.target.src = 'https://i.pravatar.cc/150';
                            }}
                        />
                        <div className="following-info">
                            <span className="username">{followed.username}</span>
                            <span className="name">{followed.fullName || followed.username}</span>
                        </div>
                        {followed.id !== currentUserId && (
                            <button 
                                className="unfollow-btn"
                                onClick={() => onUnfollow(followed.id)}
                            >
                                Unfollow
                            </button>
                        )}
                    </div>
                ))
            )}
        </div>
    );
};

export default Profile;
