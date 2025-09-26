// services/authService.js
import { apiClient } from './api.js';

export const authService = {
  // Đăng nhập
  login: async (credentials) => {
    const response = await apiClient.post('/users/login', credentials);
    return response.data;
  },
  
  // Đăng ký
  register: async (userData) => {
    const response = await apiClient.post('/users/register', userData);
    return response.data;
  },
  
  // Lấy thông tin user hiện tại - SỬA LẠI THEO BACKEND
  getCurrentUser: async () => {
    const response = await apiClient.get('/users/me');
    return response.data;
  },
  
  // Đăng xuất
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('userId');
  },
  
  // Lưu token và user info - SỬA LẠI
  setAuthData: (token, userData) => {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(userData));
    if (userData.user && userData.user.id) {
      localStorage.setItem('userId', userData.user.id);
    }
  },
  
  // Lấy token
  getToken: () => {
    return localStorage.getItem('token');
  },
  
  // Lấy user info
  getUser: () => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },
  
  // Kiểm tra đã đăng nhập chưa
  isAuthenticated: () => {
    return !!localStorage.getItem('token');
  },
  
  // Kiểm tra email tồn tại
  checkEmailExists: async (email) => {
    const response = await apiClient.get(`/users/check-email?email=${encodeURIComponent(email)}`);
    return response.data;
  },
  
  // Kiểm tra username tồn tại
  checkUsernameExists: async (username) => {
    const response = await apiClient.get(`/users/check-username?username=${encodeURIComponent(username)}`);
    return response.data;
  }
};
