package com.noxtragram.controller;

import com.noxtragram.model.dto.user.*;
import com.noxtragram.model.entity.User;
import com.noxtragram.service.UserService;
import com.noxtragram.exception.DuplicateResourceException;
import com.noxtragram.exception.OperationNotAllowedException;
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
  public ResponseEntity<User> getUserById(@PathVariable Long userId) {
    try {
      User user = userService.findById(userId);
      return ResponseEntity.ok(user);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Lấy thông tin user bằng username
   */
  @GetMapping("/username/{username}")
  public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
    try {
      User user = userService.findByUsername(username);
      return ResponseEntity.ok(user);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Lấy thông tin user bằng email
   */
  @GetMapping("/email/{email}")
  public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
    try {
      User user = userService.findByEmail(email);
      return ResponseEntity.ok(user);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  // 📋 GET MULTIPLE USERS

  /**
   * Lấy tất cả user active
   */
  @GetMapping
  public ResponseEntity<List<User>> getAllActiveUsers() {
    try {
      List<User> users = userService.findAllActiveUsers();
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
          Map.of("error", "Something went wrong", "details", e.getMessage()));
    }
  }

  // ✏️ UPDATE USER PROFILE

  /**
   * Cập nhật thông tin user
   */
  @PutMapping("/{userId}")
  public ResponseEntity<User> updateUserProfile(
      @PathVariable Long userId,
      @Valid @RequestBody User userDetails) {
    try {
      User updatedUser = userService.updateUser(userId, userDetails);
      return ResponseEntity.ok(updatedUser);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
    } catch (OperationNotAllowedException e) {
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
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  // 📸 PROFILE PICTURE MANAGEMENT

  /**
   * Upload profile picture
   */
  @PostMapping("/{userId}/profile-picture")
  public ResponseEntity<User> uploadProfilePicture(
      @PathVariable Long userId,
      @RequestParam("file") MultipartFile file) {
    try {
      User updatedUser = userService.uploadProfilePicture(userId, file);
      return ResponseEntity.ok(updatedUser);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Xóa profile picture
   */
  @DeleteMapping("/{userId}/profile-picture")
  public ResponseEntity<User> removeProfilePicture(@PathVariable Long userId) {
    try {
      User updatedUser = userService.removeProfilePicture(userId);
      return ResponseEntity.ok(updatedUser);
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
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Lấy danh sách followers của user
   */
  @GetMapping("/{userId}/followers")
  public ResponseEntity<List<User>> getFollowers(@PathVariable Long userId) {
    try {
      List<User> followers = userService.getFollowers(userId);
      return ResponseEntity.ok(followers);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Lấy danh sách following của user
   */
  @GetMapping("/{userId}/following")
  public ResponseEntity<List<User>> getFollowing(@PathVariable Long userId) {
    try {
      List<User> following = userService.getFollowing(userId);
      return ResponseEntity.ok(following);
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
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // 🔍 SEARCH USERS

  /**
   * Tìm kiếm user theo keyword (username hoặc full name)
   */
  @GetMapping("/search")
  public ResponseEntity<List<User>> searchUsers(@RequestParam String keyword) {
    try {
      List<User> users = userService.searchUsers(keyword);
      return ResponseEntity.ok(users);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Tìm kiếm user theo username
   */
  @GetMapping("/search/username")
  public ResponseEntity<List<User>> searchByUsername(@RequestParam String username) {
    try {
      List<User> users = userService.searchByUsername(username);
      return ResponseEntity.ok(users);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Tìm kiếm user theo full name
   */
  @GetMapping("/search/fullname")
  public ResponseEntity<List<User>> searchByFullName(@RequestParam String fullName) {
    try {
      List<User> users = userService.searchByFullName(fullName);
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
  public ResponseEntity<List<User>> getSuggestedUsers(
      @PathVariable Long userId,
      @RequestParam(defaultValue = "10") int limit) {
    try {
      List<User> suggestedUsers = userService.getSuggestedUsers(userId, limit);
      return ResponseEntity.ok(suggestedUsers);
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
  public ResponseEntity<User> verifyUser(@PathVariable Long userId) {
    try {
      User verifiedUser = userService.verifyUser(userId);
      return ResponseEntity.ok(verifiedUser);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Deactivate user (admin only)
   */
  @PostMapping("/{userId}/deactivate")
  public ResponseEntity<User> deactivateUser(@PathVariable Long userId) {
    try {
      User deactivatedUser = userService.deactivateUser(userId);
      return ResponseEntity.ok(deactivatedUser);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Reactivate user (admin only)
   */
  @PostMapping("/{userId}/reactivate")
  public ResponseEntity<User> reactivateUser(@PathVariable Long userId) {
    try {
      User reactivatedUser = userService.reactivateUser(userId);
      return ResponseEntity.ok(reactivatedUser);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
