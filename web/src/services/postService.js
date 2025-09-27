import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true, // For sending cookies with requests
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);


// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const postService = {
  // Create post with files
  createPost: async (formData) => {
    const response = await api.post('/posts', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  // Get single post
  getPost: async (postId) => {
    const response = await api.get(`/posts/${postId}`);
    return response.data;
  },

  // Get user posts
  getUserPosts: async (userId, page = 0, size = 10) => {
    const response = await api.get(`/posts/user/${userId}`, {
      params: { page, size }
    });
    return response.data;
  },

  // Get feed posts
  getFeed: async (page = 0, size = 10) => {
    const response = await api.get('/posts/feed', {
      params: { page, size }
    });
    return response.data;
  },

  // Get posts by hashtag
  getPostsByHashtag: async (hashtag, page = 0, size = 10) => {
    const response = await api.get(`/posts/hashtag/${hashtag}`, {
      params: { page, size }
    });
    return response.data;
  },

  // Get saved posts
  getSavedPosts: async (page = 0, size = 10) => {
    const response = await api.get('/posts/saved', {
      params: { page, size }
    });
    return response.data;
  },

  // Get popular posts
  getPopularPosts: async (page = 0, size = 10) => {
    const response = await api.get('/posts/popular', {
      params: { page, size }
    });
    return response.data;
  },

  // Update post
  updatePost: async (postId, postData) => {
    const response = await api.put(`/posts/${postId}`, postData);
    return response.data;
  },

  // Delete post
  deletePost: async (postId) => {
    const response = await api.delete(`/posts/${postId}`);
    return response.data;
  },

  // Like post
  likePost: async (postId) => {
    const response = await api.post(`/posts/${postId}/like`);
    return response.data;
  },

  // Unlike post
  unlikePost: async (postId) => {
    const response = await api.delete(`/posts/${postId}/like`);
    return response.data;
  },

  // Save post
  savePost: async (postId) => {
    const response = await api.post(`/posts/${postId}/save`);
    return response.data;
  },

  // Unsave post
  unsavePost: async (postId) => {
    const response = await api.delete(`/posts/${postId}/save`);
    return response.data;
  },

  // Get post interactions
  getPostInteractions: async (postId) => {
    const response = await api.get(`/posts/${postId}/interactions`);
    return response.data;
  }
};

export default postService;
