package com.noxtragram.service.implementation;

import com.noxtragram.exception.ResourceNotFoundException;
import com.noxtragram.exception.DuplicateResourceException;
import com.noxtragram.exception.OperationNotAllowedException;
import com.noxtragram.mapper.UserMapper;
import com.noxtragram.model.dto.request.*;
import com.noxtragram.model.dto.response.LoginResponseDTO;
import com.noxtragram.model.dto.response.UserResponseDTO;
import com.noxtragram.model.entity.User;
import com.noxtragram.repository.UserRepository;
import com.noxtragram.security.JwtUtils;
import com.noxtragram.service.FileStorageService;
import com.noxtragram.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final FileStorageService fileStorageService;
  private final UserMapper userMapper;
  private final JwtUtils jwtUtils;

  @Autowired
  public UserServiceImpl(UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      FileStorageService fileStorageService,
      UserMapper userMapper,
      JwtUtils jwtUtils) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.fileStorageService = fileStorageService;
    this.userMapper = userMapper;
    this.jwtUtils = jwtUtils;
  }

  @Override
  public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
    User user = userRepository.findByEmail(loginRequestDTO.getEmail())
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginRequestDTO.getEmail()));

    // Kiểm tra mật khẩu có hợp lệ không
    if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
      throw new OperationNotAllowedException("Invalid password");
    }

    // Kiểm tra có tài khoản nào đang học động không
    if (!user.getIsActive()) {
      throw new OperationNotAllowedException("Account is deactivated");
    }

    // Update last login
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);

    // Sử dụng JwtUtils để generate token
    String token = jwtUtils.generateJwtToken(user.getUsername());

    UserResponseDTO userResponseDTO = userMapper.toResponseDTO(user);
    return new LoginResponseDTO(token, userResponseDTO);
  }

  @Override
  public UserResponseDTO register(UserRequestDTO userRequestDTO) {
    // kiểm tra Email có tồn tại trên hệ thống không
    if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
      throw new DuplicateResourceException("Email already exists: " + userRequestDTO.getEmail());
    }

    // kiểm tra Username có tồn tại trên hệ thống không
    if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
      throw new DuplicateResourceException("Username already exists: " + userRequestDTO.getUsername());
    }

    User user = userMapper.toEntity(userRequestDTO);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setIsActive(true);
    user.setIsVerified(false);
    user.setIsPrivate(userRequestDTO.getIsPrivate() != null ? userRequestDTO.getIsPrivate() : false);

    User savedUser = userRepository.save(user);
    return userMapper.toResponseDTO(savedUser);
  }

  @Override
  public UserResponseDTO getCurrentUser(Long userId) {
    User user = findEntityById(userId);
    return userMapper.toResponseDTO(user);
  }

  @Override
  public UserResponseDTO getUserById(Long id) {
    User user = findEntityById(id);
    return userMapper.toResponseDTO(user);
  }

  @Override
  public UserResponseDTO getUserByEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    return userMapper.toResponseDTO(user);
  }

  @Override
  public UserResponseDTO getUserByUsername(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    return userMapper.toResponseDTO(user);
  }

  @Override
  public List<UserResponseDTO> getAllActiveUsers() {
    return userRepository.findAllActiveUsers().stream()
        .map(userMapper::toResponseDTO)
        .collect(Collectors.toList());
  }

  @Override
  public UserResponseDTO updateUser(Long userId, UserUpdateRequestDTO userUpdateDTO) {
    User user = findEntityById(userId);

    // Check username uniqueness if username is being updated
    if (userUpdateDTO.getUsername() != null &&
        !userUpdateDTO.getUsername().equals(user.getUsername()) &&
        userRepository.existsByUsername(userUpdateDTO.getUsername())) {
      throw new DuplicateResourceException("Username already exists: " + userUpdateDTO.getUsername());
    }

    userMapper.updateEntityFromDTO(userUpdateDTO, user);
    user.setUpdatedAt(LocalDateTime.now());

    User updatedUser = userRepository.save(user);
    return userMapper.toResponseDTO(updatedUser);
  }

  @Override
  public void deleteUser(Long userId) {
    User user = findEntityById(userId);
    user.setIsActive(false);
    user.setEmail("deleted_" + System.currentTimeMillis() + "_" + user.getEmail());
    user.setUsername("deleted_" + System.currentTimeMillis() + "_" + user.getUsername());
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);
  }

  @Override
  public void changePassword(Long userId, PasswordChangeRequestDTO passwordChangeDTO) {
    User user = findEntityById(userId);

    if (!passwordEncoder.matches(passwordChangeDTO.getCurrentPassword(), user.getPassword())) {
      throw new OperationNotAllowedException("Current password is incorrect");
    }

    user.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);
  }

  @Override
  public void resetPassword(PasswordResetRequestDTO passwordResetDTO) {
    User user = userRepository.findByEmail(passwordResetDTO.getEmail())
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + passwordResetDTO.getEmail()));

    user.setPassword(passwordEncoder.encode(passwordResetDTO.getNewPassword()));
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);
  }

  @Override
  public UserResponseDTO uploadProfilePicture(Long userId, MultipartFile file) {
    User user = findEntityById(userId);

    String fileName = fileStorageService.storeFile(file, "profiles");
    String oldProfilePicture = user.getProfilePicture();

    user.setProfilePicture(fileName);
    user.setUpdatedAt(LocalDateTime.now());

    User updatedUser = userRepository.save(user);

    if (oldProfilePicture != null) {
      fileStorageService.deleteFile(oldProfilePicture, "profiles");
    }

    return userMapper.toResponseDTO(updatedUser);
  }

  @Override
  public UserResponseDTO removeProfilePicture(Long userId) {
    User user = findEntityById(userId);

    if (user.getProfilePicture() != null) {
      fileStorageService.deleteFile(user.getProfilePicture(), "profiles");
      user.setProfilePicture(null);
      user.setUpdatedAt(LocalDateTime.now());
      User updatedUser = userRepository.save(user);
      return userMapper.toResponseDTO(updatedUser);
    }

    return userMapper.toResponseDTO(user);
  }

  // Follow system methods
  @Override
  public void followUser(Long followerId, Long followingId) {
    if (followerId.equals(followingId)) {
      throw new OperationNotAllowedException("Cannot follow yourself");
    }

    User follower = findEntityById(followerId);
    User following = findEntityById(followingId);

    if (follower.getFollowing().contains(following)) {
      throw new OperationNotAllowedException("Already following this user");
    }

    if (following.getIsPrivate()) {
      throw new OperationNotAllowedException("This account is private. Send follow request instead.");
    }

    follower.getFollowing().add(following);
    following.getFollowers().add(follower);
    userRepository.save(follower);
    userRepository.save(following);
  }

  @Override
  public void unfollowUser(Long followerId, Long followingId) {
    User follower = findEntityById(followerId);
    User following = findEntityById(followingId);

    if (!follower.getFollowing().contains(following)) {
      throw new OperationNotAllowedException("Not following this user");
    }

    follower.getFollowing().remove(following);
    following.getFollowers().remove(follower);
    userRepository.save(follower);
    userRepository.save(following);
  }

  @Override
  public boolean isFollowing(Long userId, Long targetUserId) {
    User user = findEntityById(userId);
    User targetUser = findEntityById(targetUserId);
    return user.getFollowing().contains(targetUser);
  }

  @Override
  public List<UserResponseDTO> getFollowers(Long userId) {
    User user = findEntityById(userId);
    return user.getFollowers().stream()
        .map(userMapper::toResponseDTO)
        .collect(Collectors.toList());
  }

  @Override
  public List<UserResponseDTO> getFollowing(Long userId) {
    User user = findEntityById(userId);
    return user.getFollowing().stream()
        .map(userMapper::toResponseDTO)
        .collect(Collectors.toList());
  }

  @Override
  public Integer getFollowerCount(Long userId) {
    User user = findEntityById(userId);
    return user.getFollowers().size();
  }

  @Override
  public Integer getFollowingCount(Long userId) {
    User user = findEntityById(userId);
    return user.getFollowing().size();
  }

  @Override
  public List<UserResponseDTO> searchUsers(String keyword) {
    List<User> byUsername = userRepository.findByUsernameContainingIgnoreCase(keyword);
    List<User> byFullName = userRepository.findByFullNameContainingIgnoreCase(keyword);

    byUsername.addAll(byFullName.stream()
        .filter(user -> !byUsername.contains(user))
        .collect(Collectors.toList()));

    return byUsername.stream()
        .map(userMapper::toResponseDTO)
        .collect(Collectors.toList());
  }

  @Override
  public List<UserResponseDTO> searchByUsername(String username) {
    return userRepository.findByUsernameContainingIgnoreCase(username).stream()
        .map(userMapper::toResponseDTO)
        .collect(Collectors.toList());
  }

  @Override
  public List<UserResponseDTO> searchByFullName(String fullName) {
    return userRepository.findByFullNameContainingIgnoreCase(fullName).stream()
        .map(userMapper::toResponseDTO)
        .collect(Collectors.toList());
  }

  @Override
  public List<UserResponseDTO> getSuggestedUsers(Long userId, int limit) {
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
        .map(userMapper::toResponseDTO)
        .collect(Collectors.toList());
  }

  @Override
  public UserResponseDTO verifyUser(Long userId) {
    User user = findEntityById(userId);
    user.setIsVerified(true);
    user.setUpdatedAt(LocalDateTime.now());
    User verifiedUser = userRepository.save(user);
    return userMapper.toResponseDTO(verifiedUser);
  }

  @Override
  public UserResponseDTO unverifyUser(Long userId) {
    User user = findEntityById(userId);
    user.setIsVerified(false);
    user.setUpdatedAt(LocalDateTime.now());
    User unverifiedUser = userRepository.save(user);
    return userMapper.toResponseDTO(unverifiedUser);
  }

  @Override
  public UserResponseDTO deactivateUser(Long userId) {
    User user = findEntityById(userId);
    user.setIsActive(false);
    user.setUpdatedAt(LocalDateTime.now());
    User deactivatedUser = userRepository.save(user);
    return userMapper.toResponseDTO(deactivatedUser);
  }

  @Override
  public UserResponseDTO reactivateUser(Long userId) {
    User user = findEntityById(userId);
    user.setIsActive(true);
    user.setUpdatedAt(LocalDateTime.now());
    User reactivatedUser = userRepository.save(user);
    return userMapper.toResponseDTO(reactivatedUser);
  }

  @Override
  public boolean emailExists(String email) {
    return userRepository.existsByEmail(email);
  }

  @Override
  public boolean usernameExists(String username) {
    return userRepository.existsByUsername(username);
  }

  @Override
  public long getTotalActiveUsers() {
    return userRepository.findAllActiveUsers().size();
  }

  // Private helper methods
  private User findEntityById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
  }

  private int calculateUserScore(User currentUser, User suggestedUser) {
    int score = 0;

    long mutualFollowers = currentUser.getFollowing().stream()
        .filter(suggestedUser.getFollowers()::contains)
        .count();
    score += (int) mutualFollowers * 10;

    return score;
  }
}
