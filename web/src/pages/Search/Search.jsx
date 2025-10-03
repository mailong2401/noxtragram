import React, { useState, useEffect, useCallback } from 'react';
import { Search, X, User, MapPin, Users } from 'lucide-react';
import { userService } from '../../services/userService';
import { postService } from '../../services/postService';
import './Search.css';

const SearchPage = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState({
    users: [],
    posts: [],
    hashtags: []
  });
  const [activeTab, setActiveTab] = useState('all');
  const [isLoading, setIsLoading] = useState(false);
  const [recentSearches, setRecentSearches] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);

  // Load recent searches từ localStorage
  useEffect(() => {
    const saved = localStorage.getItem('recentSearches');
    if (saved) {
      setRecentSearches(JSON.parse(saved));
    }
  }, []);

  // Lưu recent search
  const saveToRecentSearches = (query, type, data) => {
    const newSearch = {
      id: Date.now(),
      query,
      type,
      data,
      timestamp: new Date().toISOString()
    };

    const updated = [newSearch, ...recentSearches.filter(item => item.query !== query)].slice(0, 10);
    setRecentSearches(updated);
    localStorage.setItem('recentSearches', JSON.stringify(updated));
  };

  // Search function với debounce
  const performSearch = useCallback(async (query) => {
    if (!query.trim()) {
      setSearchResults({ users: [], posts: [], hashtags: [] });
      setShowSuggestions(true);
      return;
    }

    setIsLoading(true);
    try {
      // Search users
      const usersResponse = await userService.searchUsers(query);
      
      // Search posts by hashtag
      const postsResponse = await postService.getPostsByHashtag(query);

      // Extract hashtags từ posts
      const hashtags = extractHashtagsFromPosts(postsResponse.content || []);

      setSearchResults({
        users: usersResponse.content || usersResponse || [],
        posts: postsResponse.content || postsResponse || [],
        hashtags: hashtags.slice(0, 5)
      });

      // Lưu vào recent searches
      saveToRecentSearches(query, 'all', {
        userCount: usersResponse.content?.length || 0,
        postCount: postsResponse.content?.length || 0
      });

    } catch (error) {
      console.error('Search error:', error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  // Debounce search
  useEffect(() => {
    const timer = setTimeout(() => {
      if (searchQuery) {
        performSearch(searchQuery);
      }
    }, 500);

    return () => clearTimeout(timer);
  }, [searchQuery, performSearch]);

  // Extract hashtags từ posts
  const extractHashtagsFromPosts = (posts) => {
    const hashtagMap = {};
    posts.forEach(post => {
      const hashtags = post.content?.match(/#[\wÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂẾưăạảấầẩẫậắằẳẵặẹẻẽềềểếỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ]+/g) || [];
      hashtags.forEach(tag => {
        const cleanTag = tag.toLowerCase();
        hashtagMap[cleanTag] = (hashtagMap[cleanTag] || 0) + 1;
      });
    });

    return Object.entries(hashtagMap)
      .map(([tag, count]) => ({ tag, count }))
      .sort((a, b) => b.count - a.count);
  };

  // Clear search
  const clearSearch = () => {
    setSearchQuery('');
    setSearchResults({ users: [], posts: [], hashtags: [] });
    setShowSuggestions(true);
  };

  // Xóa recent search
  const removeRecentSearch = (id) => {
    const updated = recentSearches.filter(item => item.id !== id);
    setRecentSearches(updated);
    localStorage.setItem('recentSearches', JSON.stringify(updated));
  };

  // Xóa tất cả recent searches
  const clearAllRecentSearches = () => {
    setRecentSearches([]);
    localStorage.removeItem('recentSearches');
  };

  // Follow user
  const handleFollow = async (userId) => {
    try {
      await userService.followUser(userId);
      // Update UI
      setSearchResults(prev => ({
        ...prev,
        users: prev.users.map(user => 
          user.id === userId ? { ...user, isFollowing: true } : user
        )
      }));
    } catch (error) {
      console.error('Follow error:', error);
    }
  };

  // Unfollow user
  const handleUnfollow = async (userId) => {
    try {
      await userService.unfollowUser(userId);
      // Update UI
      setSearchResults(prev => ({
        ...prev,
        users: prev.users.map(user => 
          user.id === userId ? { ...user, isFollowing: false } : user
        )
      }));
    } catch (error) {
      console.error('Unfollow error:', error);
    }
  };

  // Render search results
  const renderResults = () => {
    if (!searchQuery.trim()) {
      return renderRecentSearches();
    }

    if (isLoading) {
      return (
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Đang tìm kiếm...</p>
        </div>
      );
    }

    const hasResults = searchResults.users.length > 0 || 
                      searchResults.posts.length > 0 || 
                      searchResults.hashtags.length > 0;

    if (!hasResults) {
      return (
        <div className="no-results">
          <p>Không tìm thấy kết quả cho "{searchQuery}"</p>
          <span>Hãy thử tìm kiếm với từ khóa khác</span>
        </div>
      );
    }

    return (
      <div className="search-results">
        {/* Tabs */}
        <div className="search-tabs">
          <button 
            className={`tab ${activeTab === 'all' ? 'active' : ''}`}
            onClick={() => setActiveTab('all')}
          >
            Tất cả
          </button>
          <button 
            className={`tab ${activeTab === 'users' ? 'active' : ''}`}
            onClick={() => setActiveTab('users')}
          >
            Mọi người
          </button>
          <button 
            className={`tab ${activeTab === 'tags' ? 'active' : ''}`}
            onClick={() => setActiveTab('tags')}
          >
            Hashtags
          </button>
          <button 
            className={`tab ${activeTab === 'posts' ? 'active' : ''}`}
            onClick={() => setActiveTab('posts')}
          >
            Bài viết
          </button>
        </div>

        {/* Results Content */}
        <div className="results-content">
          {(activeTab === 'all' || activeTab === 'users') && searchResults.users.length > 0 && (
            <div className="results-section">
              <h3>Mọi người</h3>
              {searchResults.users.map(user => (
                <UserResult 
                  key={user.id} 
                  user={user} 
                  onFollow={handleFollow}
                  onUnfollow={handleUnfollow}
                />
              ))}
            </div>
          )}

          {(activeTab === 'all' || activeTab === 'tags') && searchResults.hashtags.length > 0 && (
            <div className="results-section">
              <h3>Hashtags</h3>
              {searchResults.hashtags.map(({ tag, count }) => (
                <HashtagResult key={tag} tag={tag} count={count} />
              ))}
            </div>
          )}

          {(activeTab === 'all' || activeTab === 'posts') && searchResults.posts.length > 0 && (
            <div className="results-section">
              <h3>Bài viết</h3>
              <div className="posts-grid">
                {searchResults.posts.map(post => (
                  <PostResult key={post.id} post={post} />
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    );
  };

  // Render recent searches
  const renderRecentSearches = () => {
    if (recentSearches.length === 0) return null;

    return (
      <div className="recent-searches">
        <div className="recent-header">
          <h3>Tìm kiếm gần đây</h3>
          <button 
            className="clear-all-btn"
            onClick={clearAllRecentSearches}
          >
            Xóa tất cả
          </button>
        </div>
        {recentSearches.map(item => (
          <div key={item.id} className="recent-item">
            <div 
              className="recent-content"
              onClick={() => setSearchQuery(item.query)}
            >
              <Search size={16} />
              <span>{item.query}</span>
            </div>
            <button 
              className="remove-recent"
              onClick={() => removeRecentSearch(item.id)}
            >
              <X size={16} />
            </button>
          </div>
        ))}
      </div>
    );
  };

  return (
    <div className="search-page">
      {/* Search Header */}
      <div className="search-header">
        <div className="search-input-container">
          <Search className="search-icon" size={20} />
          <input
            type="text"
            placeholder="Tìm kiếm..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onFocus={() => setShowSuggestions(true)}
            className="search-input"
          />
          {searchQuery && (
            <button className="clear-btn" onClick={clearSearch}>
              <X size={16} />
            </button>
          )}
        </div>
      </div>

      {/* Search Content */}
      <div className="search-content">
        {showSuggestions && renderResults()}
      </div>
    </div>
  );
};

// User Result Component
const UserResult = ({ user, onFollow, onUnfollow }) => (
  <div className="user-result">
    <img 
      src={user.profilePicture || '/default-avatar.png'} 
      alt={user.username}
      className="user-avatar"
    />
    <div className="user-info">
      <div className="username">{user.username}</div>
      <div className="fullname">{user.fullName}</div>
      {user.bio && <div className="bio">{user.bio}</div>}
    </div>
    <div className="user-actions">
      {user.isFollowing ? (
        <button 
          className="btn-unfollow"
          onClick={() => onUnfollow(user.id)}
        >
          Đang theo dõi
        </button>
      ) : (
        <button 
          className="btn-follow"
          onClick={() => onFollow(user.id)}
        >
          Theo dõi
        </button>
      )}
    </div>
  </div>
);

// Hashtag Result Component
const HashtagResult = ({ tag, count }) => (
  <div className="hashtag-result">
    <div className="hashtag-icon">#</div>
    <div className="hashtag-info">
      <div className="hashtag-name">{tag}</div>
      <div className="hashtag-count">{count} bài viết</div>
    </div>
  </div>
);

// Post Result Component
const PostResult = ({ post }) => (
  <div className="post-result">
    <img 
      src={post.imageUrl || '/default-post.jpg'} 
      alt="Post"
      className="post-image"
    />
    <div className="post-overlay">
      <div className="post-stats">
        <span>❤️ {post.likeCount || 0}</span>
        <span>💬 {post.commentCount || 0}</span>
      </div>
    </div>
  </div>
);

export default SearchPage;
