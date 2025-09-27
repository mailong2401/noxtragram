import api from './api.js';

export const authService = {
  // Đăng nhập
  login: async (credentials) => {
    const response = await api.post('/users/login', credentials);
    return response.data;
  },
  
  // Đăng ký
  register: async (userData) => {
    const response = await api.post('/users/register', userData);
    return response.data;
  },
  
  // Lấy thông tin user hiện tại
  getCurrentUser: async () => {
    const response = await api.get('/users/me');
    return response.data;
  },
  
  // Đăng xuất
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
  
  // Lưu token và user info
  setAuthData: (token, user) => {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
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
    const response = await api.get(`/users/check-email/${email}`);
    return response.data;
  },
  
  // Kiểm tra username tồn tại
  checkUsernameExists: async (username) => {
    const response = await api.get(`/users/check-username/${username}`);
    return response.data;
  }
};
