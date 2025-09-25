// Import đúng cách - sử dụng named imports
import { apiClient, createFormData } from './api.js';

class UserService {
  
  // ============ AUTHENTICATION ============
  
  async login(loginData) {
    try {
      const response = await apiClient.post('/auth/login', loginData);
      if (response.data.token) {
        localStorage.setItem('authToken', response.data.token);
        localStorage.setItem('userId', response.data.user.id);
      }
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async register(userData) {
    try {
      const response = await apiClient.post('/auth/register', userData);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userId');
  }

  // ============ USER OPERATIONS ============

  async getCurrentUser() {
    try {
      const userId = localStorage.getItem('userId');
      if (!userId) throw new Error('User not authenticated');
      
      const response = await apiClient.get(`/users/${userId}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getUserById(id) {
    try {
      const response = await apiClient.get(`/users/${id}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getUserByEmail(email) {
    try {
      const response = await apiClient.get(`/users/email/${email}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getUserByUsername(username) {
    try {
      const response = await apiClient.get(`/users/username/${username}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getAllActiveUsers() {
    try {
      const response = await apiClient.get('/users/active');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async updateUser(userId, updateData) {
    try {
      const response = await apiClient.put(`/users/${userId}`, updateData);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async deleteUser(userId) {
    try {
      await apiClient.delete(`/users/${userId}`);
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // ============ PASSWORD MANAGEMENT ============

  async changePassword(userId, passwordData) {
    try {
      await apiClient.put(`/users/${userId}/password`, passwordData);
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async resetPassword(resetData) {
    try {
      await apiClient.post('/auth/reset-password', resetData);
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // ============ PROFILE MANAGEMENT ============

  async uploadProfilePicture(userId, file) {
    try {
      const formData = createFormData(file);
      const response = await apiClient.post(
        `/users/${userId}/profile-picture`,
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        }
      );
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async removeProfilePicture(userId) {
    try {
      const response = await apiClient.delete(`/users/${userId}/profile-picture`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // ============ FOLLOW SYSTEM ============

  async followUser(followerId, followingId) {
    try {
      await apiClient.post(`/users/${followerId}/follow/${followingId}`);
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async unfollowUser(followerId, followingId) {
    try {
      await apiClient.delete(`/users/${followerId}/follow/${followingId}`);
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async isFollowing(userId, targetUserId) {
    try {
      const response = await apiClient.get(`/users/${userId}/following/${targetUserId}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getFollowers(userId) {
    try {
      const response = await apiClient.get(`/users/${userId}/followers`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getFollowing(userId) {
    try {
      const response = await apiClient.get(`/users/${userId}/following`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getFollowerCount(userId) {
    try {
      const response = await apiClient.get(`/users/${userId}/followers/count`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getFollowingCount(userId) {
    try {
      const response = await apiClient.get(`/users/${userId}/following/count`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // ============ SEARCH ============

  async searchUsers(keyword) {
    try {
      const response = await apiClient.get(`/users/search?keyword=${encodeURIComponent(keyword)}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async searchByUsername(username) {
    try {
      const response = await apiClient.get(`/users/search/username?username=${encodeURIComponent(username)}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async searchByFullName(fullName) {
    try {
      const response = await apiClient.get(`/users/search/fullname?fullName=${encodeURIComponent(fullName)}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // ============ SUGGESTIONS ============

  async getSuggestedUsers(userId, limit = 10) {
    try {
      const response = await apiClient.get(`/users/${userId}/suggestions?limit=${limit}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // ============ ADMIN OPERATIONS ============

  async verifyUser(userId) {
    try {
      const response = await apiClient.put(`/admin/users/${userId}/verify`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async unverifyUser(userId) {
    try {
      const response = await apiClient.put(`/admin/users/${userId}/unverify`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async deactivateUser(userId) {
    try {
      const response = await apiClient.put(`/admin/users/${userId}/deactivate`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async reactivateUser(userId) {
    try {
      const response = await apiClient.put(`/admin/users/${userId}/reactivate`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // ============ UTILITY ============

  async emailExists(email) {
    try {
      const response = await apiClient.get(`/users/check-email?email=${encodeURIComponent(email)}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async usernameExists(username) {
    try {
      const response = await apiClient.get(`/users/check-username?username=${encodeURIComponent(username)}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getTotalActiveUsers() {
    try {
      const response = await apiClient.get('/admin/users/active/count');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // ============ ERROR HANDLING ============

  handleError(error) {
    if (error.response) {
      // Server responded với error status
      const message = error.response.data?.message || error.response.statusText;
      return new Error(`API Error: ${message} (${error.response.status})`);
    } else if (error.request) {
      // Request được gửi nhưng không nhận được response
      return new Error('Network error: Unable to connect to server');
    } else {
      // Something happened in setting up the request
      return new Error(error.message);
    }
  }

  // ============ TOKEN MANAGEMENT ============

  getToken() {
    return localStorage.getItem('authToken');
  }

  isAuthenticated() {
    return !!this.getToken();
  }

  getCurrentUserId() {
    return localStorage.getItem('userId');
  }
}

// Export singleton instance
export default new UserService();
