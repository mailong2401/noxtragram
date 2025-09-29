// components/ui/PostList/PostList.jsx
import React, { useState, useEffect } from 'react';
import PostCard from '../PostCard/PostCard';
import postService from '../../../services/postService';
import { useAuth } from '../../../contexts/AuthContext';
import styles from './PostList.module.css';

const PostList = ({ type = 'feed', userId = null, hashtag = null }) => {
  const { user } = useAuth();
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  const loadPosts = async (reset = false) => {
    try {
      setLoading(true);
      const currentPage = reset ? 0 : page;
      
      let response;
      switch (type) {
        case 'user':
          response = await postService.getUserPosts(userId, currentPage, 10);
          break;
        case 'hashtag':
          response = await postService.getPostsByHashtag(hashtag, currentPage, 10);
          break;
        case 'saved':
          response = await postService.getSavedPosts(currentPage, 10);
          break;
        case 'popular':
          response = await postService.getPopularPosts(currentPage, 10);
          break;
        default:
          response = await postService.getFeed(currentPage, 10);
      }

      const newPosts = response.content || response;
      
      if (reset) {
        setPosts(newPosts);
        setPage(1);
      } else {
        setPosts(prev => [...prev, ...newPosts]);
        setPage(prev => prev + 1);
      }
      
      setHasMore(!response.last && newPosts.length > 0);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPosts(true);
  }, [type, userId, hashtag]);

  const handleUpdatePost = (updatedPost) => {
    setPosts(prev => prev.map(post => 
      post.id === updatedPost.id ? updatedPost : post
    ));
  };

  if (error) {
    return <div className={styles.error}>Error loading posts: {error}</div>;
  }

  if (loading && posts.length === 0) {
    return <div className={styles.loading}>Loading posts...</div>;
  }

  return (
    <div className={styles.postList}>
      {posts.map(post => (
        <PostCard 
          key={post.id} 
          post={post} 
          onUpdate={() => handleUpdatePost(post)}
        />
      ))}
      
      {hasMore && (
        <button 
          className={styles.loadMoreBtn}
          onClick={() => loadPosts(false)}
          disabled={loading}
        >
          {loading ? 'Loading...' : 'Load More'}
        </button>
      )}
      
      {!hasMore && posts.length > 0 && (
        <div className={styles.endMessage}>No more posts to load</div>
      )}
      
      {posts.length === 0 && !loading && (
        <div className={styles.emptyMessage}>No posts found</div>
      )}
    </div>
  );
};

export default PostList;
