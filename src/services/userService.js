import { apiClient } from './api.js';

class UserService {
  
  // ============ USER PROFILE OPERATIONS ============

  async getCurrentUser() {
    try {
      const response = await apiClient.get('/users/me');
      return response.data;
    } catch (error) {
      // Fallback đến endpoint khác nếu /me không tồn tại
      if (error.response?.status === 404) {
        const response = await apiClient.get('/users/profile');
        return response.data;
      }
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

  async getUserProfile(username) {
    try {
      const response = await apiClient.get(`/users/profile/${username}`);
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

  async updateProfile(updateData) {
    try {
      const response = await apiClient.put('/users/profile', updateData);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async uploadProfilePicture(file) {
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const response = await apiClient.post(
        '/users/upload-profile-picture',
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

  async changePassword(currentPassword, newPassword) {
    try {
      const response = await apiClient.put('/users/change-password', {
        currentPassword,
        newPassword
      });
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // ============ FOLLOW SYSTEM ============

  async followUser(userIdToFollow) {
    try {
      const response = await apiClient.post(`/users/follow/${userIdToFollow}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async unfollowUser(userIdToUnfollow) {
    try {
      const response = await apiClient.post(`/users/unfollow/${userIdToUnfollow}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getFollowers(userId, page = 0, size = 20) {
    try {
      const response = await apiClient.get(`/users/${userId}/followers`, {
        params: { page, size }
      });
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getFollowing(userId, page = 0, size = 20) {
    try {
      const response = await apiClient.get(`/users/${userId}/following`, {
        params: { page, size }
      });
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async checkFollowStatus(userId) {
    try {
      const response = await apiClient.get(`/users/${userId}/follow-status`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // ============ SEARCH & DISCOVER ============

  async searchUsers(query, page = 0, size = 20) {
    try {
      const response = await apiClient.get('/users/search', {
        params: { q: query, page, size }
      });
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getSuggestedUsers(page = 0, size = 10) {
    try {
      const response = await apiClient.get('/users/suggestions', {
        params: { page, size }
      });
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // ============ USER POSTS ============

  async getUserPosts(userId, page = 0, size = 10) {
    try {
      const response = await apiClient.get(`/users/${userId}/posts`, {
        params: { page, size }
      });
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getUserLikedPosts(userId, page = 0, size = 10) {
    try {
      const response = await apiClient.get(`/users/${userId}/liked-posts`, {
        params: { page, size }
      });
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // ============ PRIVACY & SETTINGS ============

  async updatePrivacySettings(settings) {
    try {
      const response = await apiClient.put('/users/privacy-settings', settings);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async deactivateAccount() {
    try {
      const response = await apiClient.post('/users/deactivate');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // ============ ERROR HANDLING ============

  handleError(error) {
    if (error.response) {
      // Backend trả về message trong response
      const message = error.response.data?.message || 
                     error.response.data?.error || 
                     error.response.statusText;
      return new Error(`${message} (${error.response.status})`);
    } else if (error.request) {
      return new Error('Network error: Unable to connect to server. Please check your connection.');
    } else {
      return new Error(`Unexpected error: ${error.message}`);
    }
  }
}

// Export singleton instance
export const userService = new UserService();
export default userService;
