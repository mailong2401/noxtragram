// contexts/AuthContext.js
import React, { createContext, useState, useContext, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    checkAuthStatus();
  }, []);

  const checkAuthStatus = async () => {
    try {
      const token = authService.getToken();
      if (token) {
        const userData = await authService.getCurrentUser();
        setUser(userData);
      }
    } catch (error) {
      console.error('Auth check failed:', error);
      authService.logout();
    } finally {
      setLoading(false);
    }
  };

  const login = async (credentials) => {
    try {
      setError('');
      const response = await authService.login(credentials);
      
      // Backend trả về { token, user } 
      authService.setAuthData(response.token, response);
      setUser(response.user || response); // Tùy thuộc vào structure response
      
      return { success: true, data: response };
    } catch (error) {
      const errorMessage = error.response?.data?.message || error.message || 'Đăng nhập thất bại';
      setError(errorMessage);
      return { success: false, error: errorMessage };
    }
  };

  const register = async (userData) => {
    try {
      setError('');
      const result = await authService.register(userData);
      
      // Tự động đăng nhập sau khi đăng ký
      const loginResult = await login({
        email: userData.email,
        password: userData.password
      });
      
      return loginResult;
    } catch (error) {
      const errorMessage = error.response?.data?.message || error.message || 'Đăng ký thất bại';
      setError(errorMessage);
      return { success: false, error: errorMessage };
    }
  };

  const logout = () => {
    authService.logout();
    setUser(null);
    setError('');
  };

  const clearError = () => {
    setError('');
  };

  const updateUser = (userData) => {
    setUser(userData);
    // Cập nhật localStorage nếu cần
    localStorage.setItem('user', JSON.stringify(userData));
  };

  const value = {
    user,
    login,
    register,
    logout,
    updateUser,
    loading,
    error,
    clearError,
    isAuthenticated: !!user
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
