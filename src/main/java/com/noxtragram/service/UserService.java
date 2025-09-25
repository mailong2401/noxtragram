package com.noxtragram.service;

import com.noxtragram.model.dto.user.*;
import com.noxtragram.model.entity.User;
import com.noxtragram.exception.ResourceNotFoundException;
import com.noxtragram.mapper.UserMapper;
import com.noxtragram.exception.DuplicateResourceException;
import com.noxtragram.exception.OperationNotAllowedException;
import com.noxtragram.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final FileStorageService fileStorageService;

  @Autowired
  public UserService(UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      FileStorageService fileStorageService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.fileStorageService = fileStorageService;
  }

  // 👤 BASIC USER OPERATIONS

  // ✅ Thêm phương thức getCurrentUserDTO
  public UserDTO getCurrentUserDTO(Long userId) {
    User user = findById(userId);
    return UserMapper.toDTO(user);
  }

  /**
   * Tìm user bằng ID
   */
  public User findById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
  }

  /**
   * Tìm user bằng email
   */
  public User findByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
  }

  /**
   * Tìm user bằng username
   */
  public User findByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
  }

  /**
   * Lấy tất cả user active
   */
  public List<User> findAllActiveUsers() {
    return userRepository.findAllActiveUsers();
  }

  /**
   * Tạo user mới (đăng ký)
   */
  public UserDTO createUser(User user) {
    // Kiểm tra email đã tồn tại
    if (userRepository.existsByEmail(user.getEmail())) {
      throw new DuplicateResourceException("Email already exists: " + user.getEmail());
    }

    // Kiểm tra username đã tồn tại
    if (userRepository.existsByUsername(user.getUsername())) {
      throw new DuplicateResourceException("Username already exists: " + user.getUsername());
    }

    // Mã hóa password
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    // Set default values
    user.setIsActive(true);
    user.setIsVerified(false);
    user.setIsPrivate(false);

    // Lưu vào DB
    User savedUser = userRepository.save(user);

    // Trả về DTO (ẩn password)
    return UserMapper.toDTO(savedUser);
  }

  /**
   * Cập nhật thông tin user
   */
  public UserDTO updateUser(Long userId, UserUpdateDTO userUpdateDTO) {
    User user = findById(userId);

    UserMapper.updateEntityFromDTO(userUpdateDTO, user);
    user.setUpdatedAt(LocalDateTime.now());

    User updatedUser = userRepository.save(user);
    return UserMapper.toDTO(updatedUser);
  }

  /**
   * Xóa user (soft delete)
   */
  public void deleteUser(Long userId) {
    User user = findById(userId);
    user.setIsActive(false);
    user.setEmail("deleted_" + System.currentTimeMillis() + "_" + user.getEmail());
    user.setUsername("deleted_" + System.currentTimeMillis() + "_" + user.getUsername());
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);
  }

  // 🔐 AUTHENTICATION & SECURITY

  /**
   * Xác thực user login
   */
  public User authenticateUser(String email, String password) {
    User user = findByEmail(email);

    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new OperationNotAllowedException("Invalid password");
    }

    if (!user.getIsActive()) {
      throw new OperationNotAllowedException("Account is deactivated");
    }

    return user;
  }

  /**
   * Thay đổi password
   */
  public void changePassword(Long userId, String currentPassword, String newPassword) {
    User user = findById(userId);

    if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
      throw new OperationNotAllowedException("Current password is incorrect");
    }

    user.setPassword(passwordEncoder.encode(newPassword));
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);
  }

  /**
   * Reset password (quên mật khẩu)
   */
  public void resetPassword(String email, String newPassword) {
    User user = findByEmail(email);
    user.setPassword(passwordEncoder.encode(newPassword));
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);
  }

  // 📸 PROFILE MANAGEMENT

  /**
   * Upload profile picture
   */
  public User uploadProfilePicture(Long userId, MultipartFile file) {
    User user = findById(userId);

    String fileName = fileStorageService.storeFile(file, "profiles");
    String oldProfilePicture = user.getProfilePicture();

    user.setProfilePicture(fileName);
    user.setUpdatedAt(LocalDateTime.now());

    User updatedUser = userRepository.save(user);

    // Xóa ảnh cũ nếu có
    if (oldProfilePicture != null) {
      fileStorageService.deleteFile(oldProfilePicture, "profiles");
    }

    return updatedUser;
  }

  /**
   * Xóa profile picture
   */
  public User removeProfilePicture(Long userId) {
    User user = findById(userId);

    if (user.getProfilePicture() != null) {
      fileStorageService.deleteFile(user.getProfilePicture(), "profiles");
      user.setProfilePicture(null);
      user.setUpdatedAt(LocalDateTime.now());
      return userRepository.save(user);
    }

    return user;
  }

  // 👥 FOLLOW SYSTEM

  /**
   * Follow một user
   */
  public void followUser(Long followerId, Long followingId) {
    if (followerId.equals(followingId)) {
      throw new OperationNotAllowedException("Cannot follow yourself");
    }

    User follower = findById(followerId);
    User following = findById(followingId);

    if (follower.getFollowing().contains(following)) {
      throw new OperationNotAllowedException("Already following this user");
    }

    // Kiểm tra nếu user là private
    if (following.getIsPrivate()) {
      // Ở đây có thể implement follow request system
      throw new OperationNotAllowedException("This account is private. Send follow request instead.");
    }

    follower.follow(following);
    userRepository.save(follower);
  }

  /**
   * Unfollow một user
   */
  public void unfollowUser(Long followerId, Long followingId) {
    User follower = findById(followerId);
    User following = findById(followingId);

    if (!follower.getFollowing().contains(following)) {
      throw new OperationNotAllowedException("Not following this user");
    }

    follower.unfollow(following);
    userRepository.save(follower);
  }

  /**
   * Kiểm tra user A có follow user B không
   */
  public boolean isFollowing(Long userId, Long targetUserId) {
    User user = findById(userId);
    User targetUser = findById(targetUserId);
    return user.isFollowing(targetUser);
  }

  /**
   * Lấy danh sách followers của user
   */
  public List<User> getFollowers(Long userId) {
    User user = findById(userId);
    return userRepository.findFollowersByUserId(userId);
  }

  /**
   * Lấy danh sách following của user
   */
  public List<User> getFollowing(Long userId) {
    User user = findById(userId);
    return userRepository.findFollowingByUserId(userId);
  }

  /**
   * Lấy số lượng followers
   */
  public Integer getFollowerCount(Long userId) {
    User user = findById(userId);
    return user.getFollowers().size();
  }

  /**
   * Lấy số lượng following
   */
  public Integer getFollowingCount(Long userId) {
    User user = findById(userId);
    return user.getFollowing().size();
  }

  // 🔍 SEARCH & DISCOVERY

  /**
   * Tìm kiếm user theo username
   */
  public List<User> searchByUsername(String username) {
    return userRepository.findByUsernameContainingIgnoreCase(username);
  }

  /**
   * Tìm kiếm user theo full name
   */
  public List<User> searchByFullName(String fullName) {
    return userRepository.findByFullNameContainingIgnoreCase(fullName);
  }

  /**
   * Tìm kiếm user (kết hợp username và full name)
   */
  public List<User> searchUsers(String keyword) {
    List<User> byUsername = userRepository.findByUsernameContainingIgnoreCase(keyword);
    List<User> byFullName = userRepository.findByFullNameContainingIgnoreCase(keyword);

    // Kết hợp và loại bỏ trùng lặp
    byUsername.addAll(byFullName.stream()
        .filter(user -> !byUsername.contains(user))
        .toList());

    return byUsername;
  }

  // 📊 USER STATISTICS

  /**
   * Lấy suggested users để follow (dựa trên mutual friends, interests, etc.)
   */
  public List<User> getSuggestedUsers(Long userId, int limit) {
    User currentUser = findById(userId);

    // Simple algorithm: suggest users who are followed by people you follow
    return userRepository.findAllActiveUsers().stream()
        .filter(user -> !user.equals(currentUser))
        .filter(user -> !currentUser.getFollowing().contains(user))
        .sorted((u1, u2) -> {
          // Simple ranking algorithm (có thể cải tiến)
          int u1Score = calculateUserScore(currentUser, u1);
          int u2Score = calculateUserScore(currentUser, u2);
          return Integer.compare(u2Score, u1Score);
        })
        .limit(limit)
        .toList();
  }

  /**
   * Tính điểm để gợi ý user (algorithm có thể cải tiến)
   */
  private int calculateUserScore(User currentUser, User suggestedUser) {
    int score = 0;

    // Mutual followers
    long mutualFollowers = currentUser.getFollowing().stream()
        .filter(suggestedUser.getFollowers()::contains)
        .count();
    score += mutualFollowers * 10;

    // Common interests (có thể dựa trên hashtags, posts, etc.)
    // TODO: Implement based on actual data

    return score;
  }

  // 🎯 USER VERIFICATION & ADMIN OPERATIONS

  /**
   * Verify user (admin operation)
   */
  public User verifyUser(Long userId) {
    User user = findById(userId);
    user.setIsVerified(true);
    user.setUpdatedAt(LocalDateTime.now());
    return userRepository.save(user);
  }

  /**
   * Unverify user (admin operation)
   */
  public User unverifyUser(Long userId) {
    User user = findById(userId);
    user.setIsVerified(false);
    user.setUpdatedAt(LocalDateTime.now());
    return userRepository.save(user);
  }

  /**
   * Deactivate user (admin operation)
   */
  public User deactivateUser(Long userId) {
    User user = findById(userId);
    user.setIsActive(false);
    user.setUpdatedAt(LocalDateTime.now());
    return userRepository.save(user);
  }

  /**
   * Reactivate user (admin operation)
   */
  public User reactivateUser(Long userId) {
    User user = findById(userId);
    user.setIsActive(true);
    user.setUpdatedAt(LocalDateTime.now());
    return userRepository.save(user);
  }

  // 🔧 UTILITY METHODS

  /**
   * Kiểm tra email đã tồn tại
   */
  public boolean emailExists(String email) {
    return userRepository.existsByEmail(email);
  }

  /**
   * Kiểm tra username đã tồn tại
   */
  public boolean usernameExists(String username) {
    return userRepository.existsByUsername(username);
  }

  /**
   * Lấy tổng số user active
   */
  public long getTotalActiveUsers() {
    return userRepository.findAllActiveUsers().size();
  }

  /**
   * Update last login time
   */
  public void updateLastLogin(Long userId) {
    User user = findById(userId);
    // Có thể thêm field lastLogin trong User entity nếu cần
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);
  }
}
