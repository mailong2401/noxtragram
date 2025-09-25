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

  // ✅ Phương thức getCurrentUserDTO (có thể giữ lại hoặc xóa nếu không cần)
  public UserDTO getCurrentUserDTO(Long userId) {
    User user = findEntityById(userId);
    return UserMapper.toDTO(user);
  }

  /**
   * Tìm user bằng ID và trả về UserDTO
   */
  public UserDTO findById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    return UserMapper.toDTO(user);
  }

  /**
   * Tìm user entity bằng ID (dùng cho nội bộ service)
   */
  private User findEntityById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
  }

  /**
   * Tìm user bằng email và trả về UserDTO
   */
  public UserDTO findByEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    return UserMapper.toDTO(user);
  }

  /**
   * Tìm user bằng username và trả về UserDTO
   */
  public UserDTO findByUsername(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    return UserMapper.toDTO(user);
  }

  /**
   * Lấy tất cả user active dưới dạng DTO
   */
  public List<UserDTO> findAllActiveUsers() {
    return userRepository.findAllActiveUsers().stream()
        .map(UserMapper::toDTO)
        .toList();
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
    User user = findEntityById(userId);

    UserMapper.updateEntityFromDTO(userUpdateDTO, user);
    user.setUpdatedAt(LocalDateTime.now());

    User updatedUser = userRepository.save(user);
    return UserMapper.toDTO(updatedUser);
  }

  /**
   * Xóa user (soft delete)
   */
  public void deleteUser(Long userId) {
    User user = findEntityById(userId);
    user.setIsActive(false);
    user.setEmail("deleted_" + System.currentTimeMillis() + "_" + user.getEmail());
    user.setUsername("deleted_" + System.currentTimeMillis() + "_" + user.getUsername());
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);
  }

  // 🔐 AUTHENTICATION & SECURITY

  /**
   * Xác thực user login (vẫn trả về entity để Spring Security sử dụng)
   */
  public User authenticateUser(String email, String password) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

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
    User user = findEntityById(userId);

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
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    user.setPassword(passwordEncoder.encode(newPassword));
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);
  }

  // 📸 PROFILE MANAGEMENT

  /**
   * Upload profile picture và trả về DTO
   */
  public UserDTO uploadProfilePicture(Long userId, MultipartFile file) {
    User user = findEntityById(userId);

    String fileName = fileStorageService.storeFile(file, "profiles");
    String oldProfilePicture = user.getProfilePicture();

    user.setProfilePicture(fileName);
    user.setUpdatedAt(LocalDateTime.now());

    User updatedUser = userRepository.save(user);

    // Xóa ảnh cũ nếu có
    if (oldProfilePicture != null) {
      fileStorageService.deleteFile(oldProfilePicture, "profiles");
    }

    return UserMapper.toDTO(updatedUser);
  }

  /**
   * Xóa profile picture và trả về DTO
   */
  public UserDTO removeProfilePicture(Long userId) {
    User user = findEntityById(userId);

    if (user.getProfilePicture() != null) {
      fileStorageService.deleteFile(user.getProfilePicture(), "profiles");
      user.setProfilePicture(null);
      user.setUpdatedAt(LocalDateTime.now());
      User updatedUser = userRepository.save(user);
      return UserMapper.toDTO(updatedUser);
    }

    return UserMapper.toDTO(user);
  }

  // 👥 FOLLOW SYSTEM

  /**
   * Follow một user
   */
  public void followUser(Long followerId, Long followingId) {
    if (followerId.equals(followingId)) {
      throw new OperationNotAllowedException("Cannot follow yourself");
    }

    User follower = findEntityById(followerId);
    User following = findEntityById(followingId);

    if (follower.getFollowing().contains(following)) {
      throw new OperationNotAllowedException("Already following this user");
    }

    // Kiểm tra nếu user là private
    if (following.getIsPrivate()) {
      throw new OperationNotAllowedException("This account is private. Send follow request instead.");
    }

    follower.follow(following);
    userRepository.save(follower);
  }

  /**
   * Unfollow một user
   */
  public void unfollowUser(Long followerId, Long followingId) {
    User follower = findEntityById(followerId);
    User following = findEntityById(followingId);

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
    User user = findEntityById(userId);
    User targetUser = findEntityById(targetUserId);
    return user.isFollowing(targetUser);
  }

  /**
   * Lấy danh sách followers của user dưới dạng DTO
   */
  public List<UserDTO> getFollowers(Long userId) {
    User user = findEntityById(userId);
    return userRepository.findFollowersByUserId(userId).stream()
        .map(UserMapper::toDTO)
        .toList();
  }

  /**
   * Lấy danh sách following của user dưới dạng DTO
   */
  public List<UserDTO> getFollowing(Long userId) {
    User user = findEntityById(userId);
    return userRepository.findFollowingByUserId(userId).stream()
        .map(UserMapper::toDTO)
        .toList();
  }

  /**
   * Lấy số lượng followers
   */
  public Integer getFollowerCount(Long userId) {
    User user = findEntityById(userId);
    return user.getFollowers().size();
  }

  /**
   * Lấy số lượng following
   */
  public Integer getFollowingCount(Long userId) {
    User user = findEntityById(userId);
    return user.getFollowing().size();
  }

  // 🔍 SEARCH & DISCOVERY

  /**
   * Tìm kiếm user theo username dưới dạng DTO
   */
  public List<UserDTO> searchByUsername(String username) {
    return userRepository.findByUsernameContainingIgnoreCase(username).stream()
        .map(UserMapper::toDTO)
        .toList();
  }

  /**
   * Tìm kiếm user theo full name dưới dạng DTO
   */
  public List<UserDTO> searchByFullName(String fullName) {
    return userRepository.findByFullNameContainingIgnoreCase(fullName).stream()
        .map(UserMapper::toDTO)
        .toList();
  }

  /**
   * Tìm kiếm user (kết hợp username và full name) dưới dạng DTO
   */
  public List<UserDTO> searchUsers(String keyword) {
    List<User> byUsername = userRepository.findByUsernameContainingIgnoreCase(keyword);
    List<User> byFullName = userRepository.findByFullNameContainingIgnoreCase(keyword);

    // Kết hợp và loại bỏ trùng lặp
    byUsername.addAll(byFullName.stream()
        .filter(user -> !byUsername.contains(user))
        .toList());

    return byUsername.stream()
        .map(UserMapper::toDTO)
        .toList();
  }

  // 📊 USER STATISTICS

  /**
   * Lấy suggested users để follow dưới dạng DTO
   */
  public List<UserDTO> getSuggestedUsers(Long userId, int limit) {
    User currentUser = findEntityById(userId);

    return userRepository.findAllActiveUsers().stream()
        .filter(user -> !user.equals(currentUser))
        .filter(user -> !currentUser.getFollowing().contains(user))
        .sorted((u1, u2) -> {
          int u1Score = calculateUserScore(currentUser, u1);
          int u2Score = calculateUserScore(currentUser, u2);
          return Integer.compare(u2Score, u1Score);
        })
        .limit(limit)
        .map(UserMapper::toDTO)
        .toList();
  }

  private int calculateUserScore(User currentUser, User suggestedUser) {
    int score = 0;

    // Mutual followers
    long mutualFollowers = currentUser.getFollowing().stream()
        .filter(suggestedUser.getFollowers()::contains)
        .count();
    score += mutualFollowers * 10;

    return score;
  }

  // 🎯 USER VERIFICATION & ADMIN OPERATIONS

  /**
   * Verify user (admin operation) và trả về DTO
   */
  public UserDTO verifyUser(Long userId) {
    User user = findEntityById(userId);
    user.setIsVerified(true);
    user.setUpdatedAt(LocalDateTime.now());
    User verifiedUser = userRepository.save(user);
    return UserMapper.toDTO(verifiedUser);
  }

  /**
   * Unverify user (admin operation) và trả về DTO
   */
  public UserDTO unverifyUser(Long userId) {
    User user = findEntityById(userId);
    user.setIsVerified(false);
    user.setUpdatedAt(LocalDateTime.now());
    User unverifiedUser = userRepository.save(user);
    return UserMapper.toDTO(unverifiedUser);
  }

  /**
   * Deactivate user (admin operation) và trả về DTO
   */
  public UserDTO deactivateUser(Long userId) {
    User user = findEntityById(userId);
    user.setIsActive(false);
    user.setUpdatedAt(LocalDateTime.now());
    User deactivatedUser = userRepository.save(user);
    return UserMapper.toDTO(deactivatedUser);
  }

  /**
   * Reactivate user (admin operation) và trả về DTO
   */
  public UserDTO reactivateUser(Long userId) {
    User user = findEntityById(userId);
    user.setIsActive(true);
    user.setUpdatedAt(LocalDateTime.now());
    User reactivatedUser = userRepository.save(user);
    return UserMapper.toDTO(reactivatedUser);
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
    User user = findEntityById(userId);
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);
  }
}
