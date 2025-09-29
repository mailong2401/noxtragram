// components/ui/PostCard/PostCard.jsx
import React, { useState } from 'react';
import { useAuth } from '../../../contexts/AuthContext';
import postService from '../../../services/postService';
import styles from './PostCard.module.css';

const PostCard = ({ post, onUpdate, onDelete }) => {
  const { user } = useAuth();
  const [isLiked, setIsLiked] = useState(post.isLikedByCurrentUser);
  const [isSaved, setIsSaved] = useState(post.isSavedByCurrentUser);
  const [likeCount, setLikeCount] = useState(post.likeCount || 0);
  const [commentCount, setCommentCount] = useState(post.commentCount || 0);
  const [isLoading, setIsLoading] = useState(false);
  const [showFullCaption, setShowFullCaption] = useState(false);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  const MAX_CAPTION_LENGTH = 150;

  const handleLike = async () => {
    if (isLoading) return;
    
    setIsLoading(true);
    try {
      if (isLiked) {
        await postService.unlikePost(post.id);
        setLikeCount(prev => prev - 1);
      } else {
        await postService.likePost(post.id);
        setLikeCount(prev => prev + 1);
      }
      setIsLiked(!isLiked);
      onUpdate?.();
    } catch (error) {
      console.error('Error liking post:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSave = async () => {
    if (isLoading) return;
    
    setIsLoading(true);
    try {
      if (isSaved) {
        await postService.unsavePost(post.id);
      } else {
        await postService.savePost(post.id);
      }
      setIsSaved(!isSaved);
      onUpdate?.();
    } catch (error) {
      console.error('Error saving post:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleComment = () => {
    // Navigate to post detail or open comment modal
    console.log('Open comments for post:', post.id);
  };

  const handleShare = () => {
    // Implement share functionality
    if (navigator.share) {
      navigator.share({
        title: 'Check out this post',
        text: post.caption,
        url: `${window.location.origin}/post/${post.id}`
      });
    } else {
      // Fallback: copy to clipboard
      navigator.clipboard.writeText(`${window.location.origin}/post/${post.id}`);
      alert('Link copied to clipboard!');
    }
  };

  const handleImageNavigation = (direction) => {
    const images = post.imageUrls || [];
    if (direction === 'next') {
      setCurrentImageIndex((prev) => (prev + 1) % images.length);
    } else {
      setCurrentImageIndex((prev) => (prev - 1 + images.length) % images.length);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffInHours = Math.floor((now - date) / (1000 * 60 * 60));
    
    if (diffInHours < 1) {
      return 'Just now';
    } else if (diffInHours < 24) {
      return `${diffInHours}h ago`;
    } else if (diffInHours < 168) {
      return `${Math.floor(diffInHours / 24)}d ago`;
    } else {
      return date.toLocaleDateString('en-US', { 
        month: 'short', 
        day: 'numeric',
        year: diffInHours > 8760 ? 'numeric' : undefined
      });
    }
  };

  const shouldTruncateCaption = post.caption && post.caption.length > MAX_CAPTION_LENGTH;
  const displayCaption = showFullCaption 
    ? post.caption 
    : (shouldTruncateCaption ? post.caption.substring(0, MAX_CAPTION_LENGTH) + '...' : post.caption);

  const images = post.imageUrls || [];
  const hasMultipleImages = images.length > 1;
  const hasVideo = post.videoUrl;

  return (
    <div className={styles.postCard}>
      {/* Post Header */}
      <div className={styles.postHeader}>
        <div className={styles.userInfo}>
          <img 
            src={post.user?.profilePicture || '/default-avatar.png'} 
            alt={post.user?.username}
            className={styles.avatar}
          />
          <div className={styles.userDetails}>
            <span className={styles.username}>{post.user?.username}</span>
            {post.location && (
              <span className={styles.location}>{post.location}</span>
            )}
            <span className={styles.timestamp}>{formatDate(post.createdAt)}</span>
          </div>
        </div>
        
        {user?.id === post.user?.id && (
          <div className={styles.postActions}>
            <button className={styles.actionBtn}>⋯</button>
          </div>
        )}
      </div>

      {/* Post Media */}
      {(images.length > 0 || hasVideo) && (
        <div className={styles.mediaContainer}>
          {hasVideo ? (
            <video 
              controls 
              className={styles.video}
              poster={images[0]} // Use first image as poster if available
            >
              <source src={post.videoUrl} type="video/mp4" />
              Your browser does not support the video tag.
            </video>
          ) : (
            <>
              <img 
                src={images[currentImageIndex]} 
                alt={`Post by ${post.user?.username}`}
                className={styles.image}
              />
              
              {hasMultipleImages && (
                <>
                  <div className={styles.imageNavigation}>
                    <button 
                      className={styles.navBtn}
                      onClick={() => handleImageNavigation('prev')}
                    >
                      ‹
                    </button>
                    <button 
                      className={styles.navBtn}
                      onClick={() => handleImageNavigation('next')}
                    >
                      ›
                    </button>
                  </div>
                  <div className={styles.imageCounter}>
                    {currentImageIndex + 1} / {images.length}
                  </div>
                </>
              )}
            </>
          )}
        </div>
      )}

      {/* Post Actions */}
      <div className={styles.postActions}>
        <div className={styles.leftActions}>
          <button 
            className={`${styles.actionBtn} ${isLiked ? styles.liked : ''}`}
            onClick={handleLike}
            disabled={isLoading}
          >
            {isLiked ? '❤️' : '🤍'} {likeCount > 0 && likeCount}
          </button>
          
          <button 
            className={styles.actionBtn}
            onClick={handleComment}
          >
            💬 {commentCount > 0 && commentCount}
          </button>
          
          <button 
            className={styles.actionBtn}
            onClick={handleShare}
          >
            ↗️ {post.shareCount > 0 && post.shareCount}
          </button>
        </div>
        
        <button 
          className={`${styles.actionBtn} ${isSaved ? styles.saved : ''}`}
          onClick={handleSave}
          disabled={isLoading}
        >
          {isSaved ? '📚' : '📖'}
        </button>
      </div>

      {/* Post Caption */}
      {post.caption && (
        <div className={styles.caption}>
          <span className={styles.captionText}>
            <strong>{post.user?.username}</strong> {displayCaption}
          </span>
          {shouldTruncateCaption && (
            <button 
              className={styles.showMoreBtn}
              onClick={() => setShowFullCaption(!showFullCaption)}
            >
              {showFullCaption ? 'Show less' : 'Show more'}
            </button>
          )}
        </div>
      )}

      {/* Hashtags */}
      {post.hashtags && post.hashtags.length > 0 && (
        <div className={styles.hashtags}>
          {post.hashtags.map((hashtag, index) => (
            <span key={index} className={styles.hashtag}>
              #{hashtag}
            </span>
          ))}
        </div>
      )}

      {/* View Comments */}
      <button className={styles.viewCommentsBtn} onClick={handleComment}>
        View all {commentCount} comments
      </button>
    </div>
  );
};

export default PostCard;
