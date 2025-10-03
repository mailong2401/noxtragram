import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import PostCard from '../PostCard/PostCard';
import postService from '../../../services/postService';
import './PostList.css';

const PostList = ({ 
  posts: initialPosts = [], 
  onPostsUpdate,
  showUserInfo = true,
  enableInteraction = true,
  emptyMessage = "No posts found"
}) => {
  const [internalPosts, setInternalPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  // ✅ Đơn giản: chỉ fetch 1 lần khi mount
  useEffect(() => {
    const loadPosts = async () => {
      try {
        setLoading(true);
        const postsData = await postService.getFeed();
        const newPosts = postsData.content || postsData || [];
        
        setInternalPosts(newPosts);
        
        if (onPostsUpdate) {
          onPostsUpdate(newPosts);
        }
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    // Ưu tiên dùng initialPosts, nếu không có thì fetch
    if (initialPosts && initialPosts.length > 0) {
      setInternalPosts(initialPosts);
      setLoading(false);
    } else {
      loadPosts();
    }
  }, []); // ✅ Empty dependency - chỉ chạy 1 lần

  // ✅ Đơn giản handlePostUpdate
  const handlePostUpdate = (updatedPost) => {
    const updatedPosts = internalPosts.map(post => 
      post.id === updatedPost.id ? updatedPost : post
    );
    setInternalPosts(updatedPosts);
    
    if (onPostsUpdate) {
      onPostsUpdate(updatedPosts);
    }
  };

  const handlePostClick = (postId) => {
    navigate(`/posts/${postId}`);
  };

  // ... rest of component (giữ nguyên)
  if (loading) {
    return (
      <div className="post-list-loading">
        <div className="loading-spinner"></div>
        <p>Loading posts...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="post-list-error">
        <p>Error loading posts: {error}</p>
        <button onClick={() => window.location.reload()} className="retry-btn">
          Try Again
        </button>
      </div>
    );
  }

  if (!internalPosts.length) {
    return (
      <div className="post-list-empty">
        <div className="empty-icon">📷</div>
        <p>{emptyMessage}</p>
      </div>
    );
  }

  return (
    <div className="post-list">
      {internalPosts.map((post) => (
        <div 
          key={post.id} 
          className="post-list-item"
          onClick={() => handlePostClick(post.id)}
        >
          <PostCard 
            post={post}
            onUpdate={handlePostUpdate}
            showUserInfo={showUserInfo}
            enableInteraction={enableInteraction}
            compact={false}
          />
        </div>
      ))}
    </div>
  );
};

export default React.memo(PostList);
