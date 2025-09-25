package com.noxtragram.controller;

import com.noxtragram.model.dto.user.*;
import com.noxtragram.model.entity.User;
import com.noxtragram.service.UserService;
import com.noxtragram.exception.DuplicateResourceException;
import com.noxtragram.exception.OperationNotAllowedException;
import com.noxtragram.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

  @Autowired
  private UserService userService;

  // 👤 GET USER PROFILE

  /**
   * Lấy thông tin user bằng ID
   */
  @GetMapping("/{userId}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
    try {
      UserDTO userDTO = userService.findById(userId);
      return ResponseEntity.ok(userDTO);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Lấy thông tin user bằng username
   */
  @GetMapping("/username/{username}")
  public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
    try {
      UserDTO userDTO = userService.findByUsername(username);
      return ResponseEntity.ok(userDTO);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Lấy thông tin user bằng email
   */
  @GetMapping("/email/{email}")
  public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
    try {
      UserDTO userDTO = userService.findByEmail(email);
      return ResponseEntity.ok(userDTO);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // 📋 GET MULTIPLE USERS

  /**
   * Lấy tất cả user active
   */
  @GetMapping
  public ResponseEntity<List<UserDTO>> getAllActiveUsers() {
    try {
      List<UserDTO> users = userService.findAllActiveUsers();
      return ResponseEntity.ok(users);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // ➕ CREATE USER (REGISTRATION)

  /**
   * Đăng ký user mới
   */
  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
    try {
      UserDTO createdUser = userService.createUser(user);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    } catch (DuplicateResourceException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(
          Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
          Map.of("error", "Something went wrong"));
    }
  }

  // ✏️ UPDATE USER PROFILE

  /**
   * Cập nhật thông tin user
   */
  @PutMapping("/{userId}")
  public ResponseEntity<UserDTO> updateUserProfile(
      @PathVariable Long userId,
      @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
    try {
      UserDTO updatedUser = userService.updateUser(userId, userUpdateDTO);
      return ResponseEntity.ok(updatedUser);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // 🗑️ DELETE USER (SOFT DELETE)

  /**
   * Xóa user (soft delete)
   */
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
    try {
      userService.deleteUser(userId);
      return ResponseEntity.noContent().build();
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // 🔐 AUTHENTICATION & PASSWORD

  /**
   * Đăng nhập user
   */
  @PostMapping("/login")
  public ResponseEntity<User> loginUser(@RequestBody Map<String, String> credentials) {
    try {
      String email = credentials.get("email");
      String password = credentials.get("password");

      User authenticatedUser = userService.authenticateUser(email, password);
      return ResponseEntity.ok(authenticatedUser);
    } catch (OperationNotAllowedException | ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Thay đổi password
   */
  @PostMapping("/{userId}/change-password")
  public ResponseEntity<Void> changePassword(
      @PathVariable Long userId,
      @RequestBody Map<String, String> passwords) {
    try {
      String currentPassword = passwords.get("currentPassword");
      String newPassword = passwords.get("newPassword");

      userService.changePassword(userId, currentPassword, newPassword);
      return ResponseEntity.ok().build();
    } catch (OperationNotAllowedException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Reset password (quên mật khẩu)
   */
  @PostMapping("/reset-password")
  public ResponseEntity<Void> resetPassword(@RequestBody Map<String, String> request) {
    try {
      String email = request.get("email");
      String newPassword = request.get("newPassword");

      userService.resetPassword(email, newPassword);
      return ResponseEntity.ok().build();
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // 📸 PROFILE PICTURE MANAGEMENT

  /**
   * Upload profile picture
   */
  @PostMapping("/{userId}/profile-picture")
  public ResponseEntity<UserDTO> uploadProfilePicture(
      @PathVariable Long userId,
      @RequestParam("file") MultipartFile file) {
    try {
      UserDTO updatedUser = userService.uploadProfilePicture(userId, file);
      return ResponseEntity.ok(updatedUser);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Xóa profile picture
   */
  @DeleteMapping("/{userId}/profile-picture")
  public ResponseEntity<UserDTO> removeProfilePicture(@PathVariable Long userId) {
    try {
      UserDTO updatedUser = userService.removeProfilePicture(userId);
      return ResponseEntity.ok(updatedUser);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // 👥 FOLLOW SYSTEM

  /**
   * Follow một user
   */
  @PostMapping("/{followerId}/follow/{followingId}")
  public ResponseEntity<Void> followUser(
      @PathVariable Long followerId,
      @PathVariable Long followingId) {
    try {
      userService.followUser(followerId, followingId);
      return ResponseEntity.ok().build();
    } catch (OperationNotAllowedException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Unfollow một user
   */
  @PostMapping("/{followerId}/unfollow/{followingId}")
  public ResponseEntity<Void> unfollowUser(
      @PathVariable Long followerId,
      @PathVariable Long followingId) {
    try {
      userService.unfollowUser(followerId, followingId);
      return ResponseEntity.ok().build();
    } catch (OperationNotAllowedException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Kiểm tra user A có follow user B không
   */
  @GetMapping("/{userId}/is-following/{targetUserId}")
  public ResponseEntity<Boolean> isFollowing(
      @PathVariable Long userId,
      @PathVariable Long targetUserId) {
    try {
      boolean isFollowing = userService.isFollowing(userId, targetUserId);
      return ResponseEntity.ok(isFollowing);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Lấy danh sách followers của user
   */
  @GetMapping("/{userId}/followers")
  public ResponseEntity<List<UserDTO>> getFollowers(@PathVariable Long userId) {
    try {
      List<UserDTO> followers = userService.getFollowers(userId);
      return ResponseEntity.ok(followers);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Lấy danh sách following của user
   */
  @GetMapping("/{userId}/following")
  public ResponseEntity<List<UserDTO>> getFollowing(@PathVariable Long userId) {
    try {
      List<UserDTO> following = userService.getFollowing(userId);
      return ResponseEntity.ok(following);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Lấy số lượng followers và following
   */
  @GetMapping("/{userId}/follow-stats")
  public ResponseEntity<Map<String, Integer>> getFollowStats(@PathVariable Long userId) {
    try {
      Integer followerCount = userService.getFollowerCount(userId);
      Integer followingCount = userService.getFollowingCount(userId);

      return ResponseEntity.ok(Map.of(
          "followerCount", followerCount,
          "followingCount", followingCount));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // 🔍 SEARCH USERS

  /**
   * Tìm kiếm user theo keyword (username hoặc full name)
   */
  @GetMapping("/search")
  public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String keyword) {
    try {
      List<UserDTO> users = userService.searchUsers(keyword);
      return ResponseEntity.ok(users);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Tìm kiếm user theo username
   */
  @GetMapping("/search/username")
  public ResponseEntity<List<UserDTO>> searchByUsername(@RequestParam String username) {
    try {
      List<UserDTO> users = userService.searchByUsername(username);
      return ResponseEntity.ok(users);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Tìm kiếm user theo full name
   */
  @GetMapping("/search/fullname")
  public ResponseEntity<List<UserDTO>> searchByFullName(@RequestParam String fullName) {
    try {
      List<UserDTO> users = userService.searchByFullName(fullName);
      return ResponseEntity.ok(users);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // 💡 SUGGESTED USERS

  /**
   * Lấy suggested users để follow
   */
  @GetMapping("/{userId}/suggestions")
  public ResponseEntity<List<UserDTO>> getSuggestedUsers(
      @PathVariable Long userId,
      @RequestParam(defaultValue = "10") int limit) {
    try {
      List<UserDTO> suggestedUsers = userService.getSuggestedUsers(userId, limit);
      return ResponseEntity.ok(suggestedUsers);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // 🔧 UTILITY ENDPOINTS

  /**
   * Kiểm tra email đã tồn tại
   */
  @GetMapping("/check-email/{email}")
  public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
    try {
      boolean exists = userService.emailExists(email);
      return ResponseEntity.ok(exists);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Kiểm tra username đã tồn tại
   */
  @GetMapping("/check-username/{username}")
  public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
    try {
      boolean exists = userService.usernameExists(username);
      return ResponseEntity.ok(exists);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Lấy tổng số user active
   */
  @GetMapping("/stats/total-active")
  public ResponseEntity<Long> getTotalActiveUsers() {
    try {
      long total = userService.getTotalActiveUsers();
      return ResponseEntity.ok(total);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // 🎯 ADMIN OPERATIONS

  /**
   * Verify user (admin only)
   */
  @PostMapping("/{userId}/verify")
  public ResponseEntity<UserDTO> verifyUser(@PathVariable Long userId) {
    try {
      UserDTO verifiedUser = userService.verifyUser(userId);
      return ResponseEntity.ok(verifiedUser);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Deactivate user (admin only)
   */
  @PostMapping("/{userId}/deactivate")
  public ResponseEntity<UserDTO> deactivateUser(@PathVariable Long userId) {
    try {
      UserDTO deactivatedUser = userService.deactivateUser(userId);
      return ResponseEntity.ok(deactivatedUser);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Reactivate user (admin only)
   */
  @PostMapping("/{userId}/reactivate")
  public ResponseEntity<UserDTO> reactivateUser(@PathVariable Long userId) {
    try {
      UserDTO reactivatedUser = userService.reactivateUser(userId);
      return ResponseEntity.ok(reactivatedUser);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
