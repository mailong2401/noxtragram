package com.noxtragram.controller;

import com.noxtragram.model.dto.request.*;
import com.noxtragram.model.dto.response.UserResponseDTO;
import com.noxtragram.model.dto.response.LoginResponseDTO;
import org.springframework.security.core.Authentication; // ✅ THÊM IMPORT NÀY
import com.noxtragram.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  // 🔐 AUTHENTICATION ENDPOINTS

  @PostMapping("/register")
  public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO userRequestDTO) {
    UserResponseDTO userResponseDTO = userService.register(userRequestDTO);
    return ResponseEntity.ok(userResponseDTO);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
    LoginResponseDTO loginResponseDTO = userService.login(loginRequestDTO);
    return ResponseEntity.ok(loginResponseDTO);
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      // Lấy user details từ authentication
      Object principal = authentication.getPrincipal();

      if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
        String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        UserResponseDTO userResponseDTO = userService.getUserByUsername(username);
        return ResponseEntity.ok(userResponseDTO);
      } else {
        // Nếu principal là String (username)
        String username = principal.toString();
        UserResponseDTO userResponseDTO = userService.getUserByUsername(username);
        return ResponseEntity.ok(userResponseDTO);
      }
    } catch (Exception e) {
      throw new RuntimeException("Error getting current user: " + e.getMessage());
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
    UserResponseDTO userResponseDTO = userService.getUserById(id);
    return ResponseEntity.ok(userResponseDTO);
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
    UserResponseDTO userResponseDTO = userService.getUserByEmail(email);
    return ResponseEntity.ok(userResponseDTO);
  }

  @GetMapping("/username/{username}")
  public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
    UserResponseDTO userResponseDTO = userService.getUserByUsername(username);
    return ResponseEntity.ok(userResponseDTO);
  }

  @PutMapping("/{userId}")
  public ResponseEntity<UserResponseDTO> updateUser(
      @PathVariable Long userId,
      @Valid @RequestBody UserUpdateRequestDTO userUpdateDTO) {
    UserResponseDTO userResponseDTO = userService.updateUser(userId, userUpdateDTO);
    return ResponseEntity.ok(userResponseDTO);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
    userService.deleteUser(userId);
    return ResponseEntity.ok().build();
  }

  // 🔐 PASSWORD MANAGEMENT

  @PutMapping("/{userId}/password")
  public ResponseEntity<Void> changePassword(
      @PathVariable Long userId,
      @Valid @RequestBody PasswordChangeRequestDTO passwordChangeDTO) {
    userService.changePassword(userId, passwordChangeDTO);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetRequestDTO passwordResetDTO) {
    userService.resetPassword(passwordResetDTO);
    return ResponseEntity.ok().build();
  }

  // 📸 PROFILE PICTURE

  @PostMapping("/me/profile-picture")
  public ResponseEntity<UserResponseDTO> uploadProfilePicture(
      @RequestParam("file") MultipartFile file,
      Authentication authentication) {
    // Lấy username từ token (hoặc principal)
    String username = authentication.getName();
    UserResponseDTO userResponseDTO = userService.uploadProfilePicture(username, file);
    return ResponseEntity.ok(userResponseDTO);
  }

  @DeleteMapping("/{userId}/profile-picture")
  public ResponseEntity<UserResponseDTO> removeProfilePicture(@PathVariable Long userId) {
    UserResponseDTO userResponseDTO = userService.removeProfilePicture(userId);
    return ResponseEntity.ok(userResponseDTO);
  }

  // 👥 FOLLOW SYSTEM

  @PostMapping("/follow/{targetUserId}")
  public ResponseEntity<?> followUser(
      @PathVariable Long targetUserId,
      Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      // Lấy userId từ authentication (người đang đăng nhập)
      String username = authentication.getName();
      Long currentUserId = userService.getUserIdByUsername(username);

      userService.followUser(currentUserId, targetUserId);

      return ResponseEntity.ok().body(Map.of(
          "message", "Followed user successfully",
          "following", true));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Failed to follow user"));
    }
  }

  @PostMapping("/unfollow/{targetUserId}")
  public ResponseEntity<?> unfollowUser(
      @PathVariable Long targetUserId,
      Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      // Lấy userId từ authentication (người đang đăng nhập)
      String username = authentication.getName();
      Long currentUserId = userService.getUserIdByUsername(username);

      userService.unfollowUser(currentUserId, targetUserId);

      return ResponseEntity.ok().body(Map.of(
          "message", "Unfollowed user successfully",
          "following", false));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Failed to unfollow user"));
    }
  }

  @GetMapping("/is-following/{targetUserId}")
  public ResponseEntity<?> isFollowing(
      @PathVariable Long targetUserId,
      Authentication authentication) {
    try {
      if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      String username = authentication.getName();
      Long currentUserId = userService.getUserIdByUsername(username);

      boolean isFollowing = userService.isFollowing(currentUserId, targetUserId);
      return ResponseEntity.ok(Map.of("isFollowing", isFollowing));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Failed to check follow status"));
    }
  }

  @GetMapping("/{userId}/followers")
  public ResponseEntity<List<UserResponseDTO>> getFollowers(@PathVariable Long userId) {
    List<UserResponseDTO> followers = userService.getFollowers(userId);
    return ResponseEntity.ok(followers);
  }

  @GetMapping("/{userId}/following")
  public ResponseEntity<List<UserResponseDTO>> getFollowing(@PathVariable Long userId) {
    List<UserResponseDTO> following = userService.getFollowing(userId);
    return ResponseEntity.ok(following);
  }

  @GetMapping("/{userId}/follower-count")
  public ResponseEntity<Integer> getFollowerCount(@PathVariable Long userId) {
    Integer count = userService.getFollowerCount(userId);
    return ResponseEntity.ok(count);
  }

  @GetMapping("/{userId}/following-count")
  public ResponseEntity<Integer> getFollowingCount(@PathVariable Long userId) {
    Integer count = userService.getFollowingCount(userId);
    return ResponseEntity.ok(count);
  }

  // 🔍 SEARCH

  @GetMapping("/search")
  public ResponseEntity<List<UserResponseDTO>> searchUsers(@RequestParam String keyword) {
    List<UserResponseDTO> users = userService.searchUsers(keyword);
    return ResponseEntity.ok(users);
  }

  @GetMapping("/search/username")
  public ResponseEntity<List<UserResponseDTO>> searchByUsername(@RequestParam String username) {
    List<UserResponseDTO> users = userService.searchByUsername(username);
    return ResponseEntity.ok(users);
  }

  @GetMapping("/search/fullname")
  public ResponseEntity<List<UserResponseDTO>> searchByFullName(@RequestParam String fullName) {
    List<UserResponseDTO> users = userService.searchByFullName(fullName);
    return ResponseEntity.ok(users);
  }

  // 💡 SUGGESTIONS

  @GetMapping("/{userId}/suggestions")
  public ResponseEntity<List<UserResponseDTO>> getSuggestedUsers(
      @PathVariable Long userId,
      @RequestParam(defaultValue = "10") int limit) {
    List<UserResponseDTO> suggestions = userService.getSuggestedUsers(userId, limit);
    return ResponseEntity.ok(suggestions);
  }

  // 👑 ADMIN ENDPOINTS

  @PostMapping("/{userId}/verify")
  public ResponseEntity<UserResponseDTO> verifyUser(@PathVariable Long userId) {
    UserResponseDTO userResponseDTO = userService.verifyUser(userId);
    return ResponseEntity.ok(userResponseDTO);
  }

  @PostMapping("/{userId}/unverify")
  public ResponseEntity<UserResponseDTO> unverifyUser(@PathVariable Long userId) {
    UserResponseDTO userResponseDTO = userService.unverifyUser(userId);
    return ResponseEntity.ok(userResponseDTO);
  }

  @PostMapping("/{userId}/deactivate")
  public ResponseEntity<UserResponseDTO> deactivateUser(@PathVariable Long userId) {
    UserResponseDTO userResponseDTO = userService.deactivateUser(userId);
    return ResponseEntity.ok(userResponseDTO);
  }

  @PostMapping("/{userId}/reactivate")
  public ResponseEntity<UserResponseDTO> reactivateUser(@PathVariable Long userId) {
    UserResponseDTO userResponseDTO = userService.reactivateUser(userId);
    return ResponseEntity.ok(userResponseDTO);
  }

  // 🔧 UTILITY ENDPOINTS

  @GetMapping("/check-email")
  public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
    boolean exists = userService.emailExists(email);
    return ResponseEntity.ok(exists);
  }

  @GetMapping("/check-username")
  public ResponseEntity<Boolean> checkUsernameExists(@RequestParam String username) {
    boolean exists = userService.usernameExists(username);
    return ResponseEntity.ok(exists);
  }

  @GetMapping("/stats/active-count")
  public ResponseEntity<Long> getTotalActiveUsers() {
    long count = userService.getTotalActiveUsers();
    return ResponseEntity.ok(count);
  }

  @GetMapping("/active")
  public ResponseEntity<List<UserResponseDTO>> getAllActiveUsers() {
    List<UserResponseDTO> users = userService.getAllActiveUsers();
    return ResponseEntity.ok(users);
  }
}
