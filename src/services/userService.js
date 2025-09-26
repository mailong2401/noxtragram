// services/userService.js
import { apiClient, createFormData } from './api.js';

class UserService {
  
  // ============ USER OPERATIONS ============

  async getCurrentUser() {
    try {
      const response = await apiClient.get('/users/me');
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

  async updateUser(userId, updateData) {
    try {
      const response = await apiClient.put(`/users/${userId}`, updateData);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

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
      await apiClient.post(`/users/${followerId}/unfollow/${followingId}`);
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

  // ============ SEARCH ============

  async searchUsers(keyword) {
    try {
      const response = await apiClient.get(`/users/search?keyword=${encodeURIComponent(keyword)}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // ============ ERROR HANDLING ============

  handleError(error) {
    if (error.response) {
      const message = error.response.data?.message || error.response.statusText;
      return new Error(`API Error: ${message} (${error.response.status})`);
    } else if (error.request) {
      return new Error('Network error: Unable to connect to server');
    } else {
      return new Error(error.message);
    }
  }
}

// Export singleton instance
export default new UserService();
