package com.noxtragram.service;

import com.noxtragram.model.dto.request.*;
import com.noxtragram.model.dto.response.UserResponseDTO;
import com.noxtragram.model.dto.response.LoginResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

  // Authentication
  LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

  UserResponseDTO register(UserRequestDTO userRequestDTO);

  // User Operations
  UserResponseDTO getCurrentUser(Long userId);

  UserResponseDTO getUserById(Long id);

  UserResponseDTO getUserByEmail(String email);

  UserResponseDTO getUserByUsername(String username);

  List<UserResponseDTO> getAllActiveUsers();

  UserResponseDTO updateUser(Long userId, UserUpdateRequestDTO userUpdateDTO);

  void deleteUser(Long userId);

  // Password Management
  void changePassword(Long userId, PasswordChangeRequestDTO passwordChangeDTO);

  void resetPassword(PasswordResetRequestDTO passwordResetDTO);

  // Profile Management
  UserResponseDTO uploadProfilePicture(String username, MultipartFile file);

  UserResponseDTO removeProfilePicture(Long userId);

  // Follow System
  void followUser(Long followerId, Long followingId);

  void unfollowUser(Long followerId, Long followingId);

  boolean isFollowing(Long userId, Long targetUserId);

  List<UserResponseDTO> getFollowers(Long userId);

  List<UserResponseDTO> getFollowing(Long userId);

  Integer getFollowerCount(Long userId);

  Integer getFollowingCount(Long userId);

  // Search
  List<UserResponseDTO> searchUsers(String keyword);

  List<UserResponseDTO> searchByUsername(String username);

  List<UserResponseDTO> searchByFullName(String fullName);

  // Suggestions
  List<UserResponseDTO> getSuggestedUsers(Long userId, int limit);

  // Admin Operations
  UserResponseDTO verifyUser(Long userId);

  UserResponseDTO unverifyUser(Long userId);

  UserResponseDTO deactivateUser(Long userId);

  UserResponseDTO reactivateUser(Long userId);

  // Utility
  boolean emailExists(String email);

  boolean usernameExists(String username);

  long getTotalActiveUsers();
}
